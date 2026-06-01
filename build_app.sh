#!/usr/bin/env bash
# -*- coding: utf-8 -*-

source .env
LIB_DIR="lib/"

APP_VERSION=$(echo "$APP_VERSION" | cut -d'-' -f1) # Suffixe wie -SNAPSHOT oder -RC1 werden abgeschnitten
echo "Cleaned version string: $APP_VERSION"

BUILD_DIR="./BUILD"


# -------- BASE METHODS --------

jpackage_win() {
  echo "starting JPackage..."
  jpackage --input target/input \
    --name "${APP_NAME}" \
    --icon "images/app-icon.ico" \
    --main-jar "${MAIN_JAR}" \
    --app-version "${APP_VERSION}" \
    --dest "${BUILD_DIR}" \
    --java-options "--module-path \$APPDIR/$LIB_DIR" \
    --java-options "--add-modules=javafx.controls,javafx.fxml,javafx.swing" \
    --java-options "--add-reads=io.github.willena.sqlitejdbc=ALL-UNNAMED" \
    --type msi \
    --win-dir-chooser \
    --win-menu \
    --win-shortcut \
    --verbose
}

jpackage_linux_rpm() {
  echo "starting JPackage..."
  jpackage --input target/input \
    --name "${APP_NAME}" \
    --icon "images/app-icon.png" \
    --main-jar "${MAIN_JAR}" \
    --app-version "${APP_VERSION}" \
    --dest "${BUILD_DIR}" \
    --java-options "--module-path \$APPDIR/$LIB_DIR" \
    --java-options "--add-modules=javafx.controls,javafx.fxml,javafx.swing" \
    --java-options "--add-reads=io.github.willena.sqlitejdbc=ALL-UNNAMED" \
    --type "rpm" \
    --linux-menu-group "Office" \
    --linux-shortcut \
    --verbose
}

jpackage_mac() {
  echo "starting JPackage..."
  jpackage --input target/input \
    --name "${APP_NAME}" \
    --icon "images/mac-icon.icns" \
    --main-jar "${MAIN_JAR}" \
    --app-version "$(echo "$APP_VERSION" | awk -F. '{ $1=($1<1?100:$1); print $1"."$2"."$3 }')" \
    --dest "${BUILD_DIR}" \
    --java-options "--module-path \$APPDIR/$LIB_DIR" \
    --java-options "--add-modules=javafx.controls,javafx.fxml,javafx.swing" \
    --java-options "--add-reads=io.github.willena.sqlitejdbc=ALL-UNNAMED" \
    --type dmg \
    --mac-app-category "finance" \
    --verbose
}

clean_output_dir() {
  rm -rf $BUILD_DIR
}

run_maven_build() {
  mvn clean package || {
    echo "\n   (╯°□°）╯︵ ┻━┻\n"
    exit 1
  }
}

make_build_dir_and_copy_target() {
  mkdir target/input
  mkdir target/input/$LIB_DIR
  cp bb.application/target/$MAIN_JAR target/input/$MAIN_JAR
  cp bb.application/target/$LIB_DIR* target/input/$LIB_DIR
}

# -------- MAIN --------

main() {
  clean_output_dir
  run_maven_build
  make_build_dir_and_copy_target


  if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    jpackage_linux_rpm
  elif [[ "$OSTYPE" == "darwin"* ]]; then
    # Mac OSX
    jpackage_mac
  elif [[ "$OSTYPE" == "msys" ]]; then
    # Windows (MinGW / git bash)
    jpackage_win
  fi

  jpackage_status=$?
  if [ $jpackage_status -eq 0 ]; then
    exit 0
  fi

  echo "Clean up..."
  rm -rf target/input

  exit 1

}

main