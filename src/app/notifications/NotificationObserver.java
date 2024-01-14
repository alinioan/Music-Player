package app.notifications;

public interface NotificationObserver {
    /**
     * Update notifications.
     *
     * @param name the name.
     * @param description the description.
     */
    void updateNotifications(String name, String description);
}
