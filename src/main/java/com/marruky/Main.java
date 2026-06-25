package com.marruky;

import com.marruky.menu.Menu;
import com.marruky.repository.*;
import com.marruky.util.Seeder;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //Seeder seeder = new Seeder();
        //seeder.seed();
        new Menu(new RoomRepository(), new PersonRepository(),
                new ReservationRepository(), new UserRepository(),
                new ServiceRepository(), new InvoiceRepository()).loginScreen();
    }
}