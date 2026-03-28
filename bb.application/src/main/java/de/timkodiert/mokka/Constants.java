package de.timkodiert.mokka;

import java.nio.file.Path;

public class Constants {

        private Constants() {
        }

        public static final String INITIAL_AMOUNT_STRING = "0,00 €";

        public static final String OPERATION_MODE_ARGUMENT_NAME = "mode";

        public static final String DATA_DIR = ".budgetbook";
        public static final String PROPERTIES_PATH_TEMPLATE = Path.of(System.getProperty("user.home"), DATA_DIR, "application%s.properties").toString();
}
