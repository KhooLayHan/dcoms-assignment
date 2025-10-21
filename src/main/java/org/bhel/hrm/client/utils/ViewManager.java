package org.bhel.hrm.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class ViewManager {
    private static final Logger logger = LoggerFactory.getLogger(ViewManager.class);

    private ViewManager() {
        throw new UnsupportedOperationException("This class ViewManager is a utility class; it should not be instantiated.");
    }

    /**
     * Loads an FXML view and injects it into a parent container.
     * @param container The Pane (e.g., StackPane, BorderPane) to load the view into.
     * @param fxmlPath The absolute path to the FXML file (e.g., "/org/bhel/hrm/client/view/HRDashboardView.fxml").
     */
    public static void loadView(Pane container, String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(Objects.requireNonNull(ViewManager.class.getResource(fxmlPath)));
            container.getChildren().setAll(view);
        } catch (IOException e) {
            logger.error("Failed to load view: {}", fxmlPath, e);
        }
    }
}
