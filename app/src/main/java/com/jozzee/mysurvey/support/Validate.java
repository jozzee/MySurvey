package com.jozzee.mysurvey.support;

import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by Jozzee on 27/11/2558.
 */
public class Validate {

    public boolean validPassword(EditText editTextPassword,TextInputLayout layoutPassword,int minLength, int maxLength){
        if(editTextPassword.getText().toString().trim().isEmpty()){
            layoutPassword.setError("Enter your Password");
            return false;
        }
        else{
            if(editTextPassword.getText().toString().trim().length()<minLength){
                layoutPassword.setError("Your password must have at least "+maxLength +"letters");
                return false;
            }
            else if(editTextPassword.getText().toString().trim().length()>maxLength){
                layoutPassword.setError("Your password must have at most "+maxLength +" letters");
                return false;
            }
            else{
                layoutPassword.setErrorEnabled(false);
                return true;
            }

        }
    }
    public boolean validAccountName(EditText editTextName, TextInputLayout layoutName){
        if(editTextName.getText().toString().trim().isEmpty()){
            layoutName.setError("Enter Your Name");
            return false;
        }
        else if(editTextName.getText().toString().trim().length() < 1){
            layoutName.setError("Your name must have at least 1 letters");
            return false;
        }
        else if(editTextName.getText().toString().trim().length() > 128){
            layoutName.setError("Your name must have at most 128 letters");
            return false;
        }
        else{
            layoutName.setErrorEnabled(false);
            return true;
        }

    }
    public boolean validAccountName2(CharSequence charSequence, TextInputLayout layoutName){
        if(charSequence.toString().trim().isEmpty()){
            layoutName.setError("Enter Your Name");
            return false;
        }
        else if(charSequence.toString().trim().length() < 1){
            layoutName.setError("Your name must have at least 1 letters");
            return false;
        }
        else if(charSequence.toString().trim().length() > 128){
            layoutName.setError("Your name must have at most 128 letters");
            return false;
        }
        else{
            layoutName.setErrorEnabled(false);
            return true;
        }

    }
    public boolean validEmail(EditText editTextEmail, TextInputLayout layoutEmail){
        if(editTextEmail.getText().toString().trim().isEmpty()){
            layoutEmail.setError("Enter your Email");
            return false;
        }
        else {
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString().trim()).matches()){
                layoutEmail.setErrorEnabled(false);
                return true;
            } else {
                layoutEmail.setError("Email not valid");
                return false;
            }
        }
    }

    public final static boolean validEmail2(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    public boolean validSurveyName(EditText editTextSurveyName, TextInputLayout layoutSurveyName){
        if(editTextSurveyName.getText().toString().trim().isEmpty()){
            layoutSurveyName.setError("Enter your Survey name");
            return false;
        }
        else if(editTextSurveyName.getText().toString().trim().length() < 2){
            layoutSurveyName.setError("Your survey name must have at least 2 letters");
            return false;
        }
        else if(editTextSurveyName.getText().toString().trim().length() > 128){
            layoutSurveyName.setError("Your survey name must have at most 128 letters");
            return false;
        }
        else{
            layoutSurveyName.setErrorEnabled(false);
            return true;
        }
    }
    public boolean validSurveyPassword(){
        return false;
    }
    public boolean validAnswer(EditText editTextAnswer, TextInputLayout layoutAnswer){
        if(editTextAnswer.getText().toString().trim().isEmpty()){
            layoutAnswer.setError("Enter Your Answer");
            return false;
        }
        else if(editTextAnswer.getText().toString().trim().length() > 256){
            layoutAnswer.setError("Your answer must have at most 256 letters");
            return false;
        }
        else{
            layoutAnswer.setErrorEnabled(false);
            return true;
        }
    }
}
