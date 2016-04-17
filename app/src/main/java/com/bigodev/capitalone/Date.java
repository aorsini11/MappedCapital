package com.bigodev.capitalone;
import java.util.Scanner;

public class Date {
    public int day;
    public int year;
    public int month;

    public Date(String date){
        Scanner scanner = new Scanner(date);
        scanner.useDelimiter("-");
        this.year=scanner.nextInt();
        scanner.skip("-");
        this.month=scanner.nextInt();
        scanner.skip("-");
        this.day=scanner.nextInt();
        scanner.close();
    }

    //return 1 for this.date after date
    //return 0 for this.date = date
    //return -1 for this.date before date
    public int compare(Date d){
        if(this.year>d.year){
            return 1;
        } else if(this.year<d.year){
            return -1;
        } else{
            if(this.month>d.month){
                return 1;
            } else if(this.month<d.month){
                return -1;
            } else{
                if(this.day>d.day){
                    return 1;
                } else if(this.day<d.day){
                    return -1;
                } else{
                    return 0;
                }
            }
        }
    }
}