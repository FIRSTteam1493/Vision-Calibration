package frc.robot;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.*;

public class Limelight{
    private boolean hasTarget = false;
    private boolean llDriving=false; 
    double STEER_K = 0.08; //0.03;            how hard to turn toward the target
    double STEER_D = 0.17; //0.03;            how hard to turn toward the target
    double DRIVE_K = .045; //0.26;             how hard to drive fwd toward the target
    double DRIVE_D = 0.6; //0.26;             how hard to drive fwd toward the target
    double DESIRED_TARGET_AREA = 30; //13.0;  Area of the target when the robot reaches the wall
    double MAX_DRIVE = 0.5;  //0.7;        Simple speed limit so we don't drive too fast
    double tx=0,ty,ta=0,tv;
    private Notifier _notifier;
    NAVX gyro;
    Rotate rotate;

Limelight(Stick stick, Drive drive, NAVX _gyro, Rotate _rotate){
    gyro=_gyro;
    rotate = _rotate;

    SmartDashboard.putNumber("STEER_K", STEER_K);
    SmartDashboard.putNumber("STEER_D", STEER_D);
    SmartDashboard.putNumber("DRIVE_K", DRIVE_K);
    SmartDashboard.putNumber("DRIVE_D", DRIVE_D);
    SmartDashboard.putNumber("DESIRED AREA", DESIRED_TARGET_AREA);
    SmartDashboard.putNumber("MAX_DRIVE", MAX_DRIVE);

    class PeriodicRunnable implements java.lang.Runnable {
        double measuredDist=ta,errorDist=ta, preverrorDist=0, derrorDist=0;
        double errorTargets=0.5;
        double timeOnTarget;
        double errorAngle, preverrorAngle=0, derrorAngle=0;
        double forward, turn,leftinput,rightinput;
        double targetAngle;

        public void run() {
            getLimelightData();
            measuredDist=ta;
            errorAngle=tx;
            errorDist=DESIRED_TARGET_AREA-ta;
            derrorDist=errorDist-preverrorDist;
            derrorAngle=errorAngle-preverrorAngle;
               
            forward=DRIVE_K*errorDist + DRIVE_D*derrorDist;
            turn=STEER_K*errorAngle + STEER_D*derrorAngle;

            leftinput=forward+turn;
            rightinput=forward-turn;
            
            if(leftinput>MAX_DRIVE) leftinput=MAX_DRIVE;
            if(leftinput<-MAX_DRIVE) leftinput=-MAX_DRIVE;
            if(rightinput>MAX_DRIVE) rightinput=MAX_DRIVE;
            if(rightinput<-MAX_DRIVE) rightinput=-MAX_DRIVE;



            if (Math.abs(errorDist)<=errorTargets)
                timeOnTarget +=0.02;
            else 
                timeOnTarget=0.0;          
            drive.set(leftinput, rightinput);  
            preverrorDist=errorDist;
            preverrorAngle=errorAngle;


            if(timeOnTarget>0.5 || Math.abs(stick.getRawAxis(1))>0.1 ||!hasTarget){
                   stop();     
            }

        }
    }    
    _notifier = new Notifier(new PeriodicRunnable());
}

public void getLimelightData(){
    tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
    tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
    ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
    ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);

    SmartDashboard.putNumber("tv",tv);
    SmartDashboard.putNumber("tx",tx);
    SmartDashboard.putNumber("ta",ta);
    SmartDashboard.putNumber("Angle ", gyro.getAngle());


    if (tv<1.0 ) hasTarget=false;
    else hasTarget=true;
}

public void limelightDrive(){
    STEER_K = SmartDashboard.getNumber("STEER_K", 0);
    DRIVE_K = SmartDashboard.getNumber("DRIVE_K",0);
    STEER_D = SmartDashboard.getNumber("STEER_D", 0);
    DRIVE_D = SmartDashboard.getNumber("DRIVE_D",0);
    DESIRED_TARGET_AREA = SmartDashboard.getNumber("DESIRED AREA",0);
    MAX_DRIVE = SmartDashboard.getNumber("MAX_DRIVE", 0);
    double targetAngle=tx*1.1+gyro.getAngle(); // Need to work this out
    System.out.println("targetAngle="+targetAngle);
    // rotate to line up on target
//    rotate.run(targetAngle,false);
    while(rotate.isRunning){}

    if (tv < 1.0) {
      hasTarget = false;
     llDriving=false;
    }
    else{
    gyro.reset();
    hasTarget=true;    
    llDriving=true;
    _notifier.startPeriodic(0.01);
    }
}



  public void stop(){
    // rotate to 0 degrees
    rotate.run(0,false);
    while(rotate.isRunning){}
    llDriving=false;
    _notifier.stop();
}

public boolean isRunning(){
    return llDriving;
}


}