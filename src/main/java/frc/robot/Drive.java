package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Drive {
    TalonSRX bl = new TalonSRX(2);
    TalonSRX br = new TalonSRX(4);
    TalonSRX fl = new TalonSRX(1);
    TalonSRX fr = new TalonSRX(3);
  

    Drive(){

        bl.configFactoryDefault();
        br.configFactoryDefault();
        fl.configFactoryDefault();
        fr.configFactoryDefault();
        
        fl.follow(bl);fr.follow(br);

        fr.setInverted(true);br.setInverted(true);
        
        bl.configVoltageCompSaturation(12);
        br.configVoltageCompSaturation(12);
        fl.configVoltageCompSaturation(12);
        fr.configVoltageCompSaturation(12);
        
        bl.setNeutralMode(NeutralMode.Brake);
        br.setNeutralMode(NeutralMode.Brake);
        fl.setNeutralMode(NeutralMode.Brake);
        fr.setNeutralMode(NeutralMode.Brake);
    }


    public void set(double leftin, double rightin){
        bl.set(ControlMode.PercentOutput,leftin);
        br.set(ControlMode.PercentOutput,rightin);
    }

}