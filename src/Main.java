import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static Map<String, User> users = new HashMap<>();
    private static User currentUser = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }

    static class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;

        public LoginFrame() {
            setTitle("Login");
            setSize(300, 200);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(3, 2));

            JLabel usernameLabel = new JLabel("Username:");
            usernameField = new JTextField();
            JLabel passwordLabel = new JLabel("Password:");
            passwordField = new JPasswordField();

            JButton loginButton = new JButton("Login");
            JButton registerButton = new JButton("Register");

            loginButton.addActionListener(new LoginButtonListener());
            registerButton.addActionListener(new RegisterButtonListener());

            add(usernameLabel);
            add(usernameField);
            add(passwordLabel);
            add(passwordField);
            add(loginButton);
            add(registerButton);

            setVisible(true);
        }

        class LoginButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                User user = users.get(username);
                if (user != null && user.checkPassword(password)) {
                    currentUser = user;
                    dispose();
                    new MainFrame();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password.");
                }
            }
        }

        class RegisterButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (users.containsKey(username)) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Username already taken.");
                } else {
                    User newUser = new User(username, password);
                    users.put(username, newUser);
                    JOptionPane.showMessageDialog(LoginFrame.this, "Registration successful.");
                }
            }
        }
    }

    static class MainFrame extends JFrame {
        public MainFrame() {
            setTitle("Main Menu");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(4, 1));

            JButton updateProfileButton = new JButton("Update Profile");
            JButton changePasswordButton = new JButton("Change Password");
            JButton takeExamButton = new JButton("Take Exam");
            JButton logoutButton = new JButton("Logout");

            updateProfileButton.addActionListener(e -> new UpdateProfileFrame());
            changePasswordButton.addActionListener(e -> new ChangePasswordFrame());
            takeExamButton.addActionListener(e -> new ExamFrame());
            logoutButton.addActionListener(e -> {
                currentUser = null;
                dispose();
                new LoginFrame();
            });

            add(updateProfileButton);
            add(changePasswordButton);
            add(takeExamButton);
            add(logoutButton);

            setVisible(true);
        }
    }

    static class UpdateProfileFrame extends JFrame {
        private JTextField profileField;

        public UpdateProfileFrame() {
            setTitle("Update Profile");
            setSize(300, 200);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(2, 2));

            JLabel profileLabel = new JLabel("Profile Info:");
            profileField = new JTextField(currentUser.toString());
            JButton updateButton = new JButton("Update");

            updateButton.addActionListener(e -> {
                String profileInfo = profileField.getText();
                currentUser.updateProfile(profileInfo);
                JOptionPane.showMessageDialog(UpdateProfileFrame.this, "Profile updated successfully.");
                dispose();
            });

            add(profileLabel);
            add(profileField);
            add(updateButton);

            setVisible(true);
        }
    }

    static class ChangePasswordFrame extends JFrame {
        private JPasswordField oldPasswordField;
        private JPasswordField newPasswordField;

        public ChangePasswordFrame() {
            setTitle("Change Password");
            setSize(300, 200);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(3, 2));

            JLabel oldPasswordLabel = new JLabel("Old Password:");
            oldPasswordField = new JPasswordField();
            JLabel newPasswordLabel = new JLabel("New Password:");
            newPasswordField = new JPasswordField();
            JButton updateButton = new JButton("Update");

            updateButton.addActionListener(e -> {
                String oldPassword = new String(oldPasswordField.getPassword());
                String newPassword = new String(newPasswordField.getPassword());
                currentUser.updatePassword(oldPassword, newPassword);
                JOptionPane.showMessageDialog(ChangePasswordFrame.this, "Password updated successfully.");
                dispose();
            });

            add(oldPasswordLabel);
            add(oldPasswordField);
            add(newPasswordLabel);
            add(newPasswordField);
            add(updateButton);

            setVisible(true);
        }
    }

    static class ExamFrame extends JFrame {
        private List<Question> questions;
        private int currentQuestionIndex;
        private int[] userAnswers;
        private JLabel questionLabel;
        private JRadioButton[] optionButtons;
        private ButtonGroup optionGroup;
        private Timer timer;

        public ExamFrame() {
            questions = new ArrayList<>();
            questions.add(new Question("What is 2 + 2?", new String[]{"3", "4", "5"}, 2));
            questions.add(new Question("What is the capital of France?", new String[]{"Berlin", "London", "Paris"}, 3));
            // Add more questions as needed

            currentQuestionIndex = 0;
            userAnswers = new int[questions.size()];

            setTitle("Take Exam");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(6, 1));

            questionLabel = new JLabel();
            optionButtons = new JRadioButton[4];
            optionGroup = new ButtonGroup();

            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i] = new JRadioButton();
                optionGroup.add(optionButtons[i]);
                add(optionButtons[i]);
            }

            JButton nextButton = new JButton("Next");
            nextButton.addActionListener(e -> {
                if (currentQuestionIndex < questions.size()) {
                    saveAnswer();
                    currentQuestionIndex++;
                    if (currentQuestionIndex < questions.size()) {
                        displayQuestion();
                    } else {
                        submitExam();
                    }
                }
            });

            add(questionLabel);
            add(nextButton);

            displayQuestion();
            startTimer(60);

            setVisible(true);
        }

        private void displayQuestion() {
            Question question = questions.get(currentQuestionIndex);
            questionLabel.setText(question.getQuestionText());
            String[] options = question.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                if (i < options.length) {
                    optionButtons[i].setText(options[i]);
                    optionButtons[i].setVisible(true);
                } else {
                    optionButtons[i].setVisible(false);
                }
            }
        }

        private void saveAnswer() {
            for (int i = 0; i < optionButtons.length; i++) {
                if (optionButtons[i].isSelected()) {
                    userAnswers[currentQuestionIndex] = i + 1;
                    break;
                }
            }
        }

        private void startTimer(int seconds) {
            timer = new Timer(1000, new ActionListener() {
                int timeLeft = seconds;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (timeLeft > 0) {
                        setTitle("Time left: " + timeLeft + " seconds");
                        timeLeft--;
                    } else {
                        timer.stop();
                        submitExam();
                    }
                }
            });
            timer.start();
        }

        private void submitExam() {
            int score = 0;
            for (int i = 0; i < questions.size(); i++) {
                if (questions.get(i).isCorrect(userAnswers[i])) {
                    score++;
                }
            }
            JOptionPane.showMessageDialog(ExamFrame.this, "Exam submitted. Your score: " + score + "/" + questions.size());
            dispose();
        }
    }
}
