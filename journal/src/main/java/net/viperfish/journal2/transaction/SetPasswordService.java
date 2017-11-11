/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.journal2.transaction;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.AuthenticationManager;

/**
 *
 * @author shilongdai
 */
class SetPasswordService extends Service<Void> {

    private AuthenticationManager manager;
    private String newPass;

    SetPasswordService(AuthenticationManager manager, String pass) {
        this.manager = manager;
        this.newPass = pass;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                synchronized (Lock.class) {
                    manager.setPassword(newPass);
                }
                return null;
            }
        };
    }

}
