package ma.tiwtiw.core.controller.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Aspect for logging execution of service and repository Spring components.
 */
@Slf4j
public class BaseLoggingAspect {

  /**
   * Pointcut for controllers.
   */
  @Pointcut("within(ma.tiwtiw.core.controller.BaseController+)")
  public void controllerPointcut() {
  }

  /**
   * Pointcut for services.
   */
  @Pointcut("within(ma.tiwtiw.core.service.BaseService+)")
  public void servicePointcut() {
  }

  /**
   * Pointcut for mongo repository.
   */
  @Pointcut("within(org.springframework.data.mongodb.repository.MongoRepository+)")
  public void repositoryPointcut() {
  }

  /**
   * Advice that logs methods throwing exceptions.
   *
   * @param joinPoint join point for advice
   * @param e exception
   */
  @AfterThrowing(pointcut = "controllerPointcut() || servicePointcut()", throwing = "e")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
    log.error("Exception in {}.{}() with cause = {}",
        joinPoint.getTarget().getClass().getName(),
        joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
  }

  /**
   * Advice that logs when a method is entered and exited
   *
   * @param joinPoint join point for advice
   * @return result
   * @throws Throwable throws IllegalArgumentException
   */
  @Around("controllerPointcut() || servicePointcut()")
  public Object logAroundControllerOrService(ProceedingJoinPoint joinPoint) throws Throwable {
    return logAround(joinPoint, joinPoint.getTarget().getClass().getName());
  }

  /**
   * Advice that logs when a method is entered and exited
   *
   * @param joinPoint join point for advice
   * @return result
   * @throws Throwable throws IllegalArgumentException
   */
  @Around("repositoryPointcut()")
  public Object logAroundRepository(ProceedingJoinPoint joinPoint) throws Throwable {
    return logAround(joinPoint, joinPoint.getSignature().getDeclaringTypeName());
  }

  /**
   * Advice that logs when a method is entered and exited.
   *
   * @param joinPoint join point for advice
   * @param className class name
   * @return result
   * @throws Throwable throws IllegalArgumentException
   */
  private Object logAround(ProceedingJoinPoint joinPoint, String className) throws Throwable {
    if (log.isDebugEnabled()) {
      log.debug("Enter: {}.{}() with argument[s] = {}", className,
          joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }
    try {
      Object result = joinPoint.proceed();
      if (log.isDebugEnabled()) {
        log.debug("Exit: {}.{}() with result = {}", className, joinPoint.getSignature().getName(),
            result);
      }
      return result;
    } catch (IllegalArgumentException e) {
      log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()), className,
          joinPoint.getSignature().getName());
      throw e;
    }
  }
}