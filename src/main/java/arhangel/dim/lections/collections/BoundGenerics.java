package arhangel.dim.lections.collections;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BoundGenerics {

    static class Animal {
        void feed() {
            System.out.println("Animal feed()");
        }
    }

    static class Pet extends Animal {
        void call() {
            System.out.println("Pet call()");
        }
    }

    static class Cat extends Pet {
        void mew() {
            System.out.println("Cat mew()");
        }
    }

    static class Dog extends Pet {
        void bark() {
            System.out.println("Dog bark()");
        }
    }


    static void fillPets(List<Pet> pets) {
        pets.add(new Dog());
        pets.add(new Cat());
    }

    static <T> void copy(List<? super T> dest, List<? extends T> src) {

        for (T e : src) {
            dest.add(e);
        }

        // src.stream().forEach(dest::add);
    }

    public static void main(String[] args) {
        List<Cat> cats = new ArrayList<>();
        cats.add(new Cat());
        cats.add(new Cat());


        List<? extends Pet> pets = cats;


        List<Dog> dogs = new ArrayList<>();
        dogs.add(new Dog());
        dogs.add(new Dog());

        callPets(cats); // Incompatible types (compile time)
//        callPets(dogs);


        List<Animal> animals = new ArrayList<>();
        // Incompatible types
//        fillPets(animals);

    }

//    static void callPets(List<Pet> list) {
//        // Позовем домашних питомцев
//        for (Pet pet : list) {
//            pet.call();
//        }
//    }

    // Коллекция pets - поставщик данных (producer)
//    static <T extends Pet> void callPets(List<T> pets) {
//        for (T item : pets) {
//            item.call();
//        }
//
//        //pets.stream().forEach(Pet::call);
//    }


    static void callPets(List<? extends Pet> pets) {
        pets.stream().forEach(Pet::call);
    }


    // Коллекция pets - потребитель данных (consumer)
//    static void fillPets(List<? super Pet> pets) {
//        pets.add(new Dog());
//        pets.add(new Cat());
//    }


}
