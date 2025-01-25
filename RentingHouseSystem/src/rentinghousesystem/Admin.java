/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rentinghousesystem;

public class Admin extends User {
    private static Admin instance;

    Admin(int id, String name, String email, String password) {
        super(id, name, email, password);
    }

    public static Admin getInstance(int id, String name, String email, String password) {
        if (instance == null) {
            instance = new Admin(id, name, email, password);
        }
        return instance;
    }

    @Override
    public void performRoleSpecificAction() {
        System.out.println("Admin-specific action performed.");
    }
}
