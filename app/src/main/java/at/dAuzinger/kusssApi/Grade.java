package at.dAuzinger.kusssApi;

/**
 * A class to for handling Grades
 * @author David Auzinger
 * @version 1.0
 */
public class Grade {
	public static final int A = 1;
	public static final int B = 2;
	public static final int C = 3;
	public static final int D = 4;
	public static final int E = 5;
	
	LVA lva;
	DateTime date;
	float ects;
	float swst;
	
	/**
	 * Constructor for the Grade object
	 * @param lva_ The LVA the grade is for
	 * @param date_ The date of the Exam
	 * @param ects_ The number of ECTS you get for completing this course
	 * @param swst_ The number of "Semesterwochenstunden" you get for Completing this course
	 */
	public Grade(LVA lva_, DateTime date_, float ects_, float swst_)
	{
		this.lva = lva_;
		this.date = date_;
		this.ects = ects_;
		this.swst = swst_;
	}
	
	/**
	 * Returns the date of the exam
	 * @return Return The date of the exam
	 */
	public DateTime getDate()
	{
		return date;
	}
	
	/**
	 * Returns the LVA linked to the exam
	 * @return The LVA linked to the exam
	 */
	public LVA getLVA()
	{
		return lva;
	}
	
	/**
	 * Returns the ECTS you get for the course
	 * @return The ECTS you get for the course
	 */
	public float getEcts()
	{
		return ects;
	}
	
	/**
	 * Returns the "Semesterwochenstunden" you get for the course
	 * @return The "Semesterwochenstunden" you get for the course
	 */
	public float getSwst()
	{
		return swst;
	}
}
