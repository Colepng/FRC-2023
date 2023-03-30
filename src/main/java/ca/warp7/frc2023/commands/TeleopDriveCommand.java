package ca.warp7.frc2023.commands;

import static ca.warp7.frc2023.Constants.kTeleop.kDriveSingleDirectionPercent;

import ca.warp7.frc2023.Constants.kDrivetrain;
import ca.warp7.frc2023.Constants.kTeleop;
import ca.warp7.frc2023.lib.math.SensitivityGainAdjustment;
import ca.warp7.frc2023.subsystems.SwerveDrivetrainSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public class TeleopDriveCommand extends CommandBase {
    private final SwerveDrivetrainSubsystem swerveDrivetrainSubsystem;

    private final DoubleSupplier translationSup, strafeSup, rotationSup;
    private final BooleanSupplier isRobotOrientedSup, isSlowModeSup;
    private final IntSupplier POVSup;

    private double xMagnitude, yMagnitude, rotationMagnitude;

    public TeleopDriveCommand(
            SwerveDrivetrainSubsystem swerveDrivetrainSubsystem,
            DoubleSupplier translationSup,
            DoubleSupplier strafeSup,
            DoubleSupplier rotationSup,
            BooleanSupplier isRobotOrientedSup,
            BooleanSupplier isSlowModeSup,
            IntSupplier POVSup) {

        this.swerveDrivetrainSubsystem = swerveDrivetrainSubsystem;
        addRequirements(swerveDrivetrainSubsystem);

        this.translationSup = translationSup;
        this.strafeSup = strafeSup;
        this.rotationSup = rotationSup;
        this.isRobotOrientedSup = isRobotOrientedSup;
        this.isSlowModeSup = isSlowModeSup;
        this.POVSup = POVSup;
    }

    @Override
    public void execute() {

        switch (POVSup.getAsInt()) {
            case (0):
                xMagnitude = kDriveSingleDirectionPercent;
                break;
            case (90):
                yMagnitude = -kDriveSingleDirectionPercent;
                break;

            case (180):
                xMagnitude = -kDriveSingleDirectionPercent;
                break;

            case (270):
                yMagnitude = kDriveSingleDirectionPercent;
                break;

            case (-1):
                xMagnitude = SensitivityGainAdjustment.driveGainAdjustment(
                        MathUtil.applyDeadband(translationSup.getAsDouble(), kTeleop.kDriveDeadband));
                yMagnitude = SensitivityGainAdjustment.driveGainAdjustment(
                        MathUtil.applyDeadband(strafeSup.getAsDouble(), kTeleop.kDriveDeadband));
                rotationMagnitude = MathUtil.applyDeadband(rotationSup.getAsDouble(), kTeleop.kRotateDeadband);
                break;
        }

        if (isSlowModeSup.getAsBoolean()) {
            xMagnitude *= 0.5;
            yMagnitude *= 0.5;
            rotationMagnitude *= 0.5;
        }

        SmartDashboard.putNumber("x mag", xMagnitude);
        SmartDashboard.putNumber("y mag", yMagnitude);
        SmartDashboard.putNumber("rot mag", rotationMagnitude);
        if (swerveDrivetrainSubsystem.isBrakeEnabled()
                & (Math.abs(xMagnitude) > 0.9 || Math.abs(yMagnitude) > 0.9 || Math.abs(rotationMagnitude) > 0.9))
            swerveDrivetrainSubsystem.disableBrake();

        if (!swerveDrivetrainSubsystem.isBrakeEnabled()) {
            swerveDrivetrainSubsystem.drive(
                    new Translation2d(xMagnitude, yMagnitude).times(kDrivetrain.kMaxSpeed),
                    rotationMagnitude * kDrivetrain.kMaxAngularVelocity,
                    !isRobotOrientedSup.getAsBoolean(),
                    true);
        }
    }
}
