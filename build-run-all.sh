#!/bin/bash

echo "Running mvn clean install for all services..."

modules=("Common-Dtos" "order-service" "product-service" "payment-service" "user-service")
failed_modules=()

# Initial build loop
for module in "${modules[@]}"
do
  echo "🔧 Building $module..."
  (cd "$module" && mvn clean install)
  if [ $? -ne 0 ]; then
    echo "❌ Build failed for $module"
    failed_modules+=("$module")
  else
    echo "✅ $module built successfully."
  fi
done

# Check for failures
if [ ${#failed_modules[@]} -eq 0 ]; then
  echo "✅ All modules built successfully."
  exit 0
else
  echo -e "\n❗ The following modules failed to build:"
  for module in "${failed_modules[@]}"; do
    echo "  - $module"
  done

  echo -n $'\n'Would you like to retry building the failed modules? [y/N]: 
  read retry

  if [[ "$retry" =~ ^[Yy]$ ]]; then
    retry_failed=()
    for module in "${failed_modules[@]}"
    do
      echo "🔁 Retrying $module..."
      (cd "$module" && mvn clean install)
      if [ $? -ne 0 ]; then
        echo "❌ Retry failed for $module"
        retry_failed+=("$module")
      else
        echo "✅ $module built successfully on retry."
      fi
    done

    if [ ${#retry_failed[@]} -eq 0 ]; then
      echo "✅ All previously failed modules built successfully on retry."
      exit 0
    else
      echo -e "\n⛔ Some modules still failed after retry:"
      for module in "${retry_failed[@]}"; do
        echo "  - $module"
      done
      exit 1
    fi
  else
    echo "⚠️ Skipping retry. Build incomplete."
    exit 1
  fi
fi
echo
read -p "${YELLOW}Press [Enter] to exit...${RESET}"
