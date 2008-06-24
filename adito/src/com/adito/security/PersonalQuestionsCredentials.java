package com.adito.security;

public class PersonalQuestionsCredentials implements Credentials {


    int questionIndex;
    String answer;
    String username;
    
    public PersonalQuestionsCredentials(String username, int questionIndex, String answer) {
        this.questionIndex = questionIndex;
        this.answer = answer;
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public int getQuestionIndex() {
        return questionIndex;
    }

}
