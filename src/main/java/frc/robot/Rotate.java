package frc.robot;


//angle:  kp=0.0075,  kI=0.00075, kiZ = 2, Max = 0.8  
// position:  all zero
import edu.wpi.first.wpilibj.Notifier;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class Rotate{
private Drive drive;
private Stick joystick;
private NAVX gyro;
private double timeOnTarget=0;
private double timeOnTargetGoal=0.25;
private double measured_angle, targetAngle;
double rotateK=0.05,rotateD=0.5, rotateMax=0.4;
double inp_min=0.04;
double errorAllowable=0.5;

public boolean isRunning=false;

private Notifier _notifier;


double motionTargetAngle;  

Rotate(Drive _drive, Stick _joystick, NAVX _gyro){
    drive=_drive;
    joystick = _joystick;
    gyro = _gyro;

     SmartDashboard.putNumber("rotK", rotateK);
    SmartDashboard.putNumber("rotD", rotateD);
    SmartDashboard.putNumber("ToTGoal", timeOnTargetGoal);
    SmartDashboard.putNumber("Min Input", inp_min); 
    SmartDashboard.putNumber("Rotate Max", rotateMax); 
    SmartDashboard.putNumber("err Allow", errorAllowable); 
    
    
    class RotateLoop implements java.lang.Runnable {

        double errora, preverrora=0, derrora=0;
        double turn,leftinput,rightinput;
        
        public void run() {
            measured_angle=-gyro.getAngle();
         
            errora=targetAngle-measured_angle;
            derrora=errora-preverrora;

            
            turn=rotateK*errora + rotateD*derrora+inp_min;               

            leftinput=-turn;
            rightinput=turn;

// cap the motor input to max allowable, and don't let it fall below inp_min threshold (no stalling)            
            if(leftinput> rotateMax)leftinput=rotateMax;
            else if(leftinput< -rotateMax)leftinput=-rotateMax;
            else if(Math.abs(leftinput)<inp_min) leftinput=inp_min*Math.signum(leftinput);

            if(rightinput> rotateMax)rightinput=rotateMax;
            else if(rightinput< -rotateMax)rightinput=-rotateMax;
            else if(Math.abs(rightinput)<inp_min) rightinput=inp_min*Math.signum(rightinput);




            if (Math.abs(errora)<=errorAllowable)
                timeOnTarget +=0.02;
            else 
                timeOnTarget=0.0;          
  
//System.out.println("timeOnTarget="+timeOnTarget+"  ToTGoal="+timeOnTargetGoal+
 //           "  errAll"+errorAllowable+"  error"+errora);
            drive.set(leftinput,rightinput);
            preverrora=errora;
            writePIDVars(measured_angle, errora);

            if(timeOnTarget>timeOnTargetGoal || 
                Math.abs(joystick.getRawAxis(1))>0.1 ){
                   stop();     
            }

        }
    }

    _notifier = new Notifier(new RotateLoop());
}



public void run(double _targetAngle, boolean reset){
    if (reset) gyro.reset();
    System.out.println("Start Rotate");
    rotateK = SmartDashboard.getNumber("rotK", 0);
    rotateD = SmartDashboard.getNumber("rotD", 0);
    timeOnTargetGoal = SmartDashboard.getNumber("ToTGoal", 0);
    inp_min = SmartDashboard.getNumber("Min Input", 0); 
    rotateMax = SmartDashboard.getNumber("Rotate Max", 0); 
    errorAllowable = SmartDashboard.getNumber("err Allow", 0); 

    targetAngle=_targetAngle;
//    gyro.reset();
    isRunning=true;
    _notifier.startPeriodic(0.01);
}


public void stop(){
    isRunning=false;
    _notifier.stop();
    System.out.println("Stop Rotate");
}



public void writePIDVars(double measured, double error){
        SmartDashboard.putNumber("Measured", measured);
        SmartDashboard.putNumber("Error", error);
    }


}





