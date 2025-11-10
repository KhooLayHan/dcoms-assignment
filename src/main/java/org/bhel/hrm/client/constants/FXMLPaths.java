package org.bhel.hrm.client.constants;

public class FXMLPaths {
    private FXMLPaths() {
        throw new UnsupportedOperationException("FXMLPaths is a utility class and should not be instantiated.");
    }

    public static final String BASE = "org/bhel/hrm/client/view/";

    private static final String EXTENSION = ".fxml";

    public static final String LOGIN = BASE + "LoginView" + EXTENSION;

    public static final String EMPLOYEE_MANAGEMENT = BASE + "EmployeeManagementView" + EXTENSION;

    public static class Dialogs {
        private Dialogs() {
            throw new UnsupportedOperationException("FXMLPaths.Dialogs is a utility class and should not be instantiated.");
        }

        public static final String DIALOG = BASE + "dialogs/";

        public static final String EMPLOYEE_FORM = DIALOG + "EmployeeFormView" + EXTENSION;
    }
}
