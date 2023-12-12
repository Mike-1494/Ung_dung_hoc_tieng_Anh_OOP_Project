package App;

public class Task {
    private int priority;
    private String taskName;
    private String taskProgress;

    public Task(int priority, String taskName, String taskProgress) {
        this.taskName = taskName;
        this.priority = priority;
        this.taskProgress = taskProgress;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTaskProgress(){
        return this.taskProgress;
    }

    public void setTaskProgress(String progress){
        this.taskProgress = progress;
    }
}
