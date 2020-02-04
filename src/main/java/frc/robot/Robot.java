
package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends TimedRobot {
  Stick stick = new Stick(0);
  Drive drive = new Drive();
  NAVX gyro = new NAVX();
  Rotate rotate = new Rotate(drive, stick, gyro);

  Limelight limelight = new Limelight(stick, drive,gyro,rotate);

  @Override
  public void robotInit() {
    gyro.reset();
  }


  @Override
  public void teleopPeriodic() {

    stick.readStick();
    limelight.getLimelightData();
    
//    if (stick.getButton(1) && !stick.getPrevButton(1)) limelight.limelightDrive();
if (stick.getButton(1) && !stick.getPrevButton(1)) gyro.reset();

  //  if (stick.getButton(2) && !stick.getPrevButton(2)) rotate.run(90,true);

    if(!limelight.isRunning() && !rotate.isRunning) drive.set(stick.getLeft(),stick.getRight());
    SmartDashboard.putNumber("Angle ", -gyro.getAngle());
 
  }



  
}
