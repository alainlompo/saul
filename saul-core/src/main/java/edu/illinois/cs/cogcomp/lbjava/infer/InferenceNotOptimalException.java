package edu.illinois.cs.cogcomp.lbjava.infer;


/**
  * Exceptions of this type are thrown by the {@link edu.illinois.cs.cogcomp.lbjava.infer.ILPInference} class when
  * the selected {@link edu.illinois.cs.cogcomp.lbjava.infer.ILPSolver} did not successfully find the optimal
  * solution to the inference problem.  Instances of this class contain a
  * reference to the {@link edu.illinois.cs.cogcomp.lbjava.infer.ILPSolver} instance so that the user can, for
  * instance, call the {@link edu.illinois.cs.cogcomp.lbjava.infer.ILPSolver#write(StringBuffer)} method.
  *
  * @author Nick Rizzolo
 **/
public class InferenceNotOptimalException extends Exception
{
  /** The ILP algorithm and problem representation that failed. */
  private ILPSolver solver;
  /** The head object of the inference problem. */
  private Object head;


  /**
    * Initializing constructor.
    *
    * @param solver The ILP algorithm and problem representation that failed.
    * @param head   The head object of the inference problem.
   **/
  public InferenceNotOptimalException(ILPSolver solver, Object head) {
    super(
        "Failed to solve inference problem with the following head object: "
        + head);
    this.solver = solver;
    this.head = head;
  }


  /** Retrieves the ILP problem instance, {@link #solver}. */
  public ILPSolver getSolver() { return solver; }


  /** Retrieves the head object, {@link #head}. */
  public Object getHead() { return head; }
}

