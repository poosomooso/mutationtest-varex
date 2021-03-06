package util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import benchmark.heuristics.TestRunListener;

public class SSHOMListener extends RunListener {
  Set<Description> homTests;
  List<Set<Description>> fomTests = new LinkedList<>();
  Set<Description> currentTests;
  int numTests = 0;
  
  public TestRunListener testRunListener = null;
  
  	@Override
	public void testStarted(Description description) throws Exception {
		if (testRunListener != null) {
			testRunListener.testStarted(description);
		}
	}
  	
  	public void testStarted(String tClass, String testName) {
  		if (testRunListener != null) {
			testRunListener.testStarted(tClass, testName);
		}
  	}
  
  public void signalHOMBegin() {
    currentTests = new HashSet<>();
  }

  public void signalHOMEnd() {
    homTests = currentTests;
    currentTests = null;
  }

  public void signalFOMBegin() {
    currentTests = new HashSet<>();
  }

  public void signalFOMEnd() {
    fomTests.add(currentTests);
    currentTests = null;
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
    super.testFailure(failure);
//    failure.getException().printStackTrace();
//    System.err.println(failure.getDescription());
    if (failure.getDescription().getDisplayName().equals("Test mechanism")) {
      // this is from the other junit listener
    } else {
      currentTests.add(failure.getDescription());
    }
  }

  @Override
  public void testAssumptionFailure(Failure failure) {
    super.testAssumptionFailure(failure);
    currentTests.add(failure.getDescription());
  }

  @Override
  public void testIgnored(Description description) throws Exception {
    super.testIgnored(description);
  }

  public Set<Description> getHomTests() {
    return Collections.unmodifiableSet(homTests);
  }

  public List<Set<Description>> getFomTests() {
    return Collections.unmodifiableList(fomTests.stream().map(
        Collections::unmodifiableSet).collect(
        Collectors.toList()));
  }

}

