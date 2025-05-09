#!/bin/bash

# --------- Color Setup ---------
RED=$(tput setaf 1 2>/dev/null || echo "")
GREEN=$(tput setaf 2 2>/dev/null || echo "")
YELLOW=$(tput setaf 3 2>/dev/null || echo "")
CYAN=$(tput setaf 6 2>/dev/null || echo "")
RESET=$(tput sgr0 2>/dev/null || echo "")

# --------- All Modules ---------
ALL_MODULES=("Common-Dtos" "order-service" "product-service" "payment-service" "eureka-server")
COMMON_MODULE="Common-Dtos"
MICROSERVICES=()

# Build list of microservices (everything except Common-Dtos)
for mod in "${ALL_MODULES[@]}"; do
  if [[ "$mod" != "$COMMON_MODULE" ]]; then
    MICROSERVICES+=("$mod")
  fi
done

# --------- Show Menu ---------
echo "${CYAN}📦 Available microservices to redeploy:${RESET}"
for i in "${!ALL_MODULES[@]}"; do
  printf "  %s%2d) %s${RESET}\n" "$YELLOW" $((i+1)) "${ALL_MODULES[$i]}"
done

# --------- User Input ---------
echo
read -p "${CYAN}Enter service numbers (comma-separated, e.g., 1,3) or press [Enter] to deploy ALL: ${RESET}" input

if [[ "$input" == "0" ]]; then
  echo "${YELLOW}🚪 Cancelled by user.${RESET}"
  exit 0
fi

IFS=',' read -ra selections <<< "$input"
selected_modules=()

if [[ -z "$input" ]]; then
  # Default: build Common-Dtos + all microservices
  selected_modules=("${ALL_MODULES[@]}")
else
  for sel in "${selections[@]}"; do
    sel_trimmed=$(echo "$sel" | xargs)
    if [[ "$sel_trimmed" =~ ^[0-9]+$ ]] && [ "$sel_trimmed" -ge 1 ] && [ "$sel_trimmed" -le "${#ALL_MODULES[@]}" ]; then
      selected_modules+=("${ALL_MODULES[$((sel_trimmed-1))]}")
    else
      echo "${RED}❌ Invalid selection: '$sel_trimmed'.${RESET}"
      exit 1
    fi
  done

  # If Common-Dtos is selected, auto-expand to all
  if [[ " ${selected_modules[*]} " =~ " $COMMON_MODULE " ]]; then
    selected_modules=("${ALL_MODULES[@]}")
    echo "${YELLOW}🔁 'Common-Dtos' selected — building all modules.${RESET}"
  fi
fi

# --------- Status Tracking ---------
declare -A mvn_status
declare -A docker_status

# --------- Step 1: Maven Build ---------
echo -e "\n${CYAN}🔨 Starting Maven build...${RESET}"
build_failed=false

for module in "${selected_modules[@]}"; do
  echo "${YELLOW}➡ Building $module...${RESET}"
  (cd "$module" && mvn clean install -DskipTests -q)
  if [ $? -eq 0 ]; then
    mvn_status["$module"]="success"
    echo "${GREEN}✅ Maven build succeeded for $module${RESET}"
  else
    mvn_status["$module"]="fail"
    build_failed=true
    echo "${RED}❌ Maven build failed for $module${RESET}"
  fi
done

# --------- Summary Function ---------
show_summary() {
  echo -e "\n${CYAN}📋 Build Summary:${RESET}"

  echo "${CYAN}🧪 Maven Build Status:${RESET}"
  for module in "${selected_modules[@]}"; do
    status="${mvn_status["$module"]}"
    color="${GREEN}"
    [[ "$status" == "fail" ]] && color="${RED}"
    printf "  %s%-20s : %s%s\n" "$color" "$module" "$status" "$RESET"
  done

  if [ "$build_failed" = true ]; then
    echo -e "\n${YELLOW}⚠️ Skipping Docker deployment due to Maven build failure.${RESET}"
  else
    echo "${CYAN}🐳 Docker Deploy Status:${RESET}"
    for module in "${selected_modules[@]}"; do
      if [[ "$module" == "$COMMON_MODULE" ]]; then continue; fi
      service_name=$(echo "$module" | tr '[:upper:]' '[:lower:]')
      status="${docker_status["$service_name"]}"
      case "$status" in
        success) color="${GREEN}" ;;
        fail) color="${RED}" ;;
        skipped) color="${YELLOW}" ;;
        *) color="${RESET}" ;;
      esac
      printf "  %s%-20s : %s%s\n" "$color" "$service_name" "$status" "$RESET"
    done
  fi
}

# --------- Exit Early If Build Fails ---------
if [ "$build_failed" = true ]; then
  show_summary
  echo
  read -p "${YELLOW}Press [Enter] to exit...${RESET}"
  exit 1
fi

# --------- Step 2: Docker Deploy ---------
echo -e "\n${CYAN}🐳 Rebuilding Docker images...${RESET}"

for module in "${selected_modules[@]}"; do
  if [[ "$module" == "$COMMON_MODULE" ]]; then continue; fi
  service_name=$(echo "$module" | tr '[:upper:]' '[:lower:]')
  echo "${YELLOW}➡ Deploying $service_name...${RESET}"
  docker-compose build "$service_name" &>/dev/null
  if [ $? -eq 0 ]; then
    docker-compose up -d --no-deps --build "$service_name" &>/dev/null
    if [ $? -eq 0 ]; then
      docker_status["$service_name"]="success"
      echo "${GREEN}✅ $service_name deployed successfully${RESET}"
    else
      docker_status["$service_name"]="fail"
      echo "${RED}❌ Failed to restart $service_name${RESET}"
    fi
  else
    docker_status["$service_name"]="fail"
    echo "${RED}❌ Docker build failed for $service_name${RESET}"
  fi
done

# --------- Final Summary ---------
show_summary
echo
read -p "${YELLOW}Press [Enter] to exit...${RESET}"
