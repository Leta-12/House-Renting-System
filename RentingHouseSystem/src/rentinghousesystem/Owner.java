/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rentinghousesystem;

public class Owner extends User {
    public Owner(int id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public void performRoleSpecificAction() {
        System.out.println("Owner-specific action performed.");
    }

    public void addHouse(House house) {
        // Add house logic
    }
}
