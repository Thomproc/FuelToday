package fr.univpau.fueltoday;

public class Service {
    private String serviceName;
    private boolean isChecked;

    public Service(String serviceName) {
        this.serviceName = serviceName;
        this.isChecked = false;
    }

    public Service(String serviceName, boolean isChecked) {
        this.serviceName = serviceName;
        this.isChecked = isChecked;
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void clicked() {
        this.isChecked = !this.isChecked;
    }
}
