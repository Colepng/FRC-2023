package ca.warp7.frc2023.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LimelightSubsystem extends SubsystemBase {
    private SwerveDrivetrainSubsystem swerveDrivetrainSubsystem;
    private final NetworkTable limelight;
    private final NetworkTableEntry botposeTargetspaceEntery;
    //    private final double[] botpose;
    private final double[] botposeTargetspace;
    private double x, y, yaw;

    public LimelightSubsystem(SwerveDrivetrainSubsystem swerveDrivetrainSubsystem) {
        swerveDrivetrainSubsystem = swerveDrivetrainSubsystem;
        limelight = NetworkTableInstance.getDefault().getTable("limelight");
        botposeTargetspaceEntery = limelight.getEntry("botpose_targetspace");
        botposeTargetspace = botposeTargetspaceEntery.getDoubleArray(new double[6]);
        x = botposeTargetspace[0];
        y = botposeTargetspace[1];
        yaw = botposeTargetspace[5];
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getYaw() {
        return yaw;
    }

    public Command AlignRotation() {
        double rotatePerSecond = MathUtil.clamp(yaw, -3, 3) * -1.0;
        return run(() -> swerveDrivetrainSubsystem.drive(new Translation2d(0.0, 0.0), rotatePerSecond, true, true));
    }
                        new Translation2d(0.0, 0.0),
                        rotatePerSecond / 10.0,
                        true,
                        true)
        );
    }

    @Override
    public void periodic() {
        x = botposeTargetspace[0];
        y = botposeTargetspace[1];
        yaw = botposeTargetspace[5];
    }
}


