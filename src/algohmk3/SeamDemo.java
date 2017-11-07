/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algohmk3;

/**
 *
 * @author dubielsw
 */
public class SeamDemo {

    public static void main(String[] args) {

        UWECImage image = new UWECImage("test2.jpg");
        UWECImage base = new UWECImage("test2.jpg");
        
        base.openNewDisplayWindow();
        image.openNewDisplayWindow();


        for (int i = 0; i < 150; i++) {
            Seam.horizontalSeamShrink(image);

        }

        for (int i = 0; i < 150; i++) {
            Seam.verticalSeamShrink(image);

        }

        System.out.println("DONE");
        
        
    }
    
}
