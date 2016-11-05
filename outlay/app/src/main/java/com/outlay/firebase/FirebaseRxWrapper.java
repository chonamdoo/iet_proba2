package com.outlay.firebase;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.outlay.domain.model.User;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by bmelnychuk on 10/26/16.
 */

public class FirebaseRxWrapper {
    private FirebaseAuth firebaseAuth;

    @Inject
    public FirebaseRxWrapper() {
        //TODO shoud I provide as param
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public Observable<String> getUserToken(FirebaseUser firebaseUser) {
        return Observable.create(subscriber -> {
            Task<GetTokenResult> task = firebaseUser.getToken(true);

            task.addOnCompleteListener(resultTask -> {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    subscriber.onNext(token);
                    subscriber.onCompleted();
                } else {
                    Exception e = task.getException();
                    subscriber.onError(e);
                }
            });
        });
    }

    public Observable<AuthResult> signUp(String email, String password) {
        return Observable.create(subscriber -> {
            Task<AuthResult> task = firebaseAuth.createUserWithEmailAndPassword(email, password);
            task.addOnCompleteListener(resultTask -> {
                if (task.isSuccessful()) {
                    AuthResult authResult = task.getResult();
                    subscriber.onNext(authResult);
                    subscriber.onCompleted();
                } else {
                    Exception e = task.getException();
                    subscriber.onError(e);
                }
            });
        });
    }

    public Observable<AuthResult> signIn(String email, String password) {
        return Observable.create(subscriber -> {
            Task<AuthResult> task = firebaseAuth.signInWithEmailAndPassword(email, password);
            task.addOnCompleteListener(resultTask -> {
                if (task.isSuccessful()) {
                    AuthResult authResult = task.getResult();
                    subscriber.onNext(authResult);
                    subscriber.onCompleted();
                } else {
                    Exception e = task.getException();
                    subscriber.onError(e);
                }
            });
        });
    }

    public Observable<AuthResult> signIn(String email, String password, FirebaseUser firebaseUser) {
        if(firebaseUser == null) {
            return signIn(email, password);
        } else {
            return Observable.create(subscriber -> {
                Task<AuthResult> task = firebaseUser.linkWithCredential(EmailAuthProvider.getCredential(email, password));
                task.addOnCompleteListener(resultTask -> {
                    if (task.isSuccessful()) {
                        AuthResult authResult = task.getResult();
                        subscriber.onNext(authResult);
                        subscriber.onCompleted();
                    } else {
                        Exception e = task.getException();
                        subscriber.onError(e);
                    }
                });
            });
        }
    }

    public Observable<AuthResult> signInAnonymously() {
        return Observable.create(subscriber -> {
            Task<AuthResult> task = firebaseAuth.signInAnonymously();
            task.addOnCompleteListener(resultTask -> {
                if (task.isSuccessful()) {
                    AuthResult authResult = task.getResult();
                    subscriber.onNext(authResult);
                    subscriber.onCompleted();
                } else {
                    Exception e = task.getException();
                    subscriber.onError(e);
                }
            });
        });
    }


    public Observable<Void> resetPassword(User user) {
        return Observable.create(subscriber -> {
            Task<Void> task = firebaseAuth.sendPasswordResetEmail(user.getEmail());
            task.addOnCompleteListener(resultTask -> {
                if (task.isSuccessful()) {
                    subscriber.onCompleted();
                } else {
                    Exception e = task.getException();
                    subscriber.onError(e);
                }
            });
        });
    }
}
