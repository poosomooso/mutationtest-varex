package util;

import gov.nasa.jpf.annotation.Conditional;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class SSHOMRunner {
  private       ConditionalMutationWrapper targetClasses;
  private final Class[]                    testClasses;

  public SSHOMRunner(Class targetClass, Class testClass) {
    this.targetClasses = new ConditionalMutationWrapper(targetClass);
    this.testClasses = new Class[] { testClass };
  }

  public SSHOMRunner(Class[] targetClasses, Class[] testClasses) {
    this.targetClasses = new ConditionalMutationWrapper(targetClasses);
    this.testClasses = Arrays.copyOf(testClasses, testClasses.length);
  }

  public SSHOMListener runJunitOnHOMAndFOMs(String... mutants)
      throws IllegalAccessException, NoSuchFieldException {
    JUnitCore jUnitCore = new JUnitCore();
    SSHOMListener sshomListener = runJunitOnHOM(mutants);
    jUnitCore.addListener(sshomListener);

    //foms
    for (String s : mutants) {
      targetClasses.resetMutants();
      targetClasses.setMutant(s);
      sshomListener.signalFOMBegin();
      jUnitCore.run(testClasses);
      sshomListener.signalFOMEnd();
    }
    return sshomListener;
  }

  public SSHOMListener runJunitOnHOM(String... mutants)
      throws IllegalAccessException, NoSuchFieldException {
    JUnitCore jUnitCore = new JUnitCore();
    SSHOMListener sshomListener = new SSHOMListener();
    jUnitCore.addListener(sshomListener);
    // hom
    targetClasses.resetMutants();
    for (String s : mutants) {
      targetClasses.setMutant(s);
    }
    sshomListener.signalHOMBegin();
    jUnitCore.run(testClasses);
    sshomListener.signalHOMEnd();

    return sshomListener;
  }


  public Collection<String> getMutants() {
    return targetClasses.getMutants();
  }
}