import models.*;
import play.Application;
import play.GlobalSettings;

import java.util.List;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        if (Prepod.find.all().isEmpty()) {
            Prepod prepod = new Prepod("admin", "Сэр", "Босс", "123123");
            try {
                prepod.save();
                System.out.println("DONE: Аккаунт администратора создан!");
            } catch (Exception e) {
                System.out.println("ERROR: Невозможно создать аккаунт администратора!");
            }

            //ТЕСТОВЫЕ ЗАПИСИ

            for (int i = 1; i <= 5; i++) {
                StudGroup studGroup = new StudGroup("Тестовая группа " + i, prepod);
                try {
                    studGroup.save();
                    System.out.println("MSG: Сохранена тестовая группа");
                } catch (Exception e) {
                    System.out.println("ERROR: Группа не сохранена!");
                }
            }



            StudGroup group = StudGroup.find.byId("5");
            String n = "Иванов Петр; Яковчук Евгений; Добринец Иван; Ширыкалов Артем; Вафина Рената; Журба Таисия; Модонов Глеб; Еремеев Владимир";
            String[] stus_names = n.split("; ");
            for (String stud : stus_names) {
                String[] name = stud.split(" ");
                Student newStudent = new Student(name[0], name[1], group);
                try{
                    newStudent.save();
                    System.out.println("MSG: Сохранен студент");
                } catch(Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
            }

            List<Student> studsList = Student.find.where()
                    .eq("studStudGroup", group)
                    .findList();

            for(Student stud: studsList) {
                for(int i = 0; i<25; i++) {
                    Plus newPlus = new Plus(stud);
                    try{
                        newPlus.save();
                        System.out.println("MSG: Сохранен плюс");
                    } catch(Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                }
            }

            for(int i = 0; i<12; i++) {
                Lab newLab = new Lab("Лабораторная " + i, new java.util.Date(), group);
                try{
                    newLab.save();
                    System.out.println("MSG: Сохраненa лаба");
                } catch(Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
            }

            Lab newLab = new Lab("Лабораторная X", new java.util.Date(), group);
            try{
                newLab.save();
                System.out.println("MSG: Сохраненa лаба");
            } catch(Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }

            for(Student stud: studsList) {
                DoneLab done = new DoneLab(new java.util.Date(), stud, newLab);
                try{
                    done.save();
                    System.out.println("MSG: Сохраненa сданная лаба");
                } catch(Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
            }

            Lab newLab2 = new Lab("Лабораторная X2", new java.util.Date(), group);
            try{
                newLab2.save();
                System.out.println("MSG: Сохраненa лаба");
            } catch(Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }

            int counter = 0;
            for(Student stud: studsList) {
                if (counter<4) {
                    DoneLab done = new DoneLab(new java.util.Date(), stud, newLab2);
                    try {
                        done.save();
                        counter++;
                        System.out.println("MSG: Сохраненa сданная лаба2");
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                }
            }

        } else {System.out.println("MSG: Администратор уже есть, первичное заполнение исполнено");}
    }
}