package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SPI;


public class NAVX {
    AHRS ahrs = new AHRS(SPI.Port.kMXP);
    NAVX(){
    }

    public double getAngle(){
        double angle;
        angle=ahrs.getAngle();
        return angle;
    }

    public void reset(){
        ahrs.reset();
    }
}