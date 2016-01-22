package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 19/11/2558.
 */
public class ViewAnswerBean {
    int noQuestion;
    String question;
    int answerType;
    String answer;
    int noChoice;

    public ViewAnswerBean() {
    }

    public ViewAnswerBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString,JsonObject.class);
        if(jsonObject.get("noQuestion")!= null){
            this.noQuestion = jsonObject.get("noQuestion").getAsInt();
        }
        if(jsonObject.get("question")!= null){
            this.question = jsonObject.get("question").getAsString();
        }
        if(jsonObject.get("answerType")!= null){
            this.answerType = jsonObject.get("answerType").getAsInt();
        }
        if(jsonObject.get("answer")!= null){
            this.answer = jsonObject.get("answer").getAsString();
        }
        if(jsonObject.get("noChoice")!= null){
            this.noChoice = jsonObject.get("noChoice").getAsInt();
        }
    }

    public ViewAnswerBean(int noQuestion, String question, int answerType, String answer, int noChoice) {
        this.noQuestion = noQuestion;
        this.question = question;
        this.answerType = answerType;
        this.answer = answer;
        this.noChoice = noChoice;
    }

    public int getNoQuestion() {
        return noQuestion;
    }

    public void setNoQuestion(int noQuestion) {
        this.noQuestion = noQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getAnswerType() {
        return answerType;
    }

    public void setAnswerType(int answerType) {
        this.answerType = answerType;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getNoChoice() {
        return noChoice;
    }

    public void setNoChoice(int noChoice) {
        this.noChoice = noChoice;
    }


}
/*
select	Questions.no_question,
		Questions.question_name,
		Questions.answer_type,
		Answer.answer_data,
		Choice.no_choice
from ((Answer join Questions on Questions.question_ID = Answer.question_ID)
		full outer join Choice on Choice.choice_ID = Answer.choice_ID)
where answer_by = 'Netivit' and answer_date = '2015-11-18 14:06'
order by Questions.no_question
*/
