import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Exam implements Serializable {
    private List<Question> questions;
    private int[] userAnswers;
    private int timeLimit; // in seconds
    private boolean isSubmitted;

    public Exam(List<Question> questions, int timeLimit) {
        this.questions = questions;
        this.userAnswers = new int[questions.size()];
        this.timeLimit = timeLimit;
        this.isSubmitted = false;
    }

    public void startExam() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!isSubmitted) {
                    System.out.println("Time's up! Auto-submitting your answers...");
                    submitExam();
                }
            }
        };
        timer.schedule(task, timeLimit * 1000);
    }

    public void submitExam() {
        isSubmitted = true;
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).isCorrect(userAnswers[i])) {
                score++;
            }
        }
        System.out.println("Exam submitted. Your score: " + score + "/" + questions.size());
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setUserAnswer(int questionIndex, int answer) {
        userAnswers[questionIndex] = answer;
    }
}
