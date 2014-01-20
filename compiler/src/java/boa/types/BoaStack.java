package boa.types;

/**
 * A {@link BoaType} representing a stack of values.
 * 
 * @author rdyer
 */
public class BoaStack extends BoaType {
	private final BoaType type;

	/**
	 * Construct a {@link BoaStack}.
	 */
	public BoaStack() {
		this(null);
	}

	/**
	 * Construct a {@link BoaStack}.
	 * 
	 * @param boaType
	 *            A {@link BoaType} representing the type of the values in
	 *            this stack
	 */
	public BoaStack(final BoaType boaType) {
		this.type = boaType;
	}

	/** {@inheritDoc} */
	@Override
	public boolean assigns(final BoaType that) {
		// if that is a function, check the return value
		if (that instanceof BoaFunction)
			return this.assigns(((BoaFunction) that).getType());

		// otherwise, if that is not a stack, forget it
		if (!(that instanceof BoaStack))
			return false;

		// same for the value type
		if (!((BoaStack) that).type.assigns(this.type))
			return false;

		// ok
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean accepts(final BoaType that) {
		// if that is a function, check the return value
		if (that instanceof BoaFunction)
			return this.assigns(((BoaFunction) that).getType());

		// otherwise, if that is not a stack, forget it
		if (!(that instanceof BoaStack))
			return false;

		// same for the value type
		if (!this.type.accepts(((BoaStack) that).type))
			return false;

		// ok
		return true;
	}

	/**
	 * Get the type of the values of this stack.
	 * 
	 * @return A {@link BoaType} representing the type of the values of this
	 *         stack
	 */
	public BoaType getType() {
		return this.type;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "stack of " + this.type;
	}

	/** {@inheritDoc} */
	@Override
	public String toJavaType() {
		return "java.util.Stack<" + this.type.toBoxedJavaType() + ">";
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.type == null ? 0 : this.type.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final BoaStack other = (BoaStack) obj;
		if (this.type == null) {
			if (other.type != null)
				return false;
		} else if (!this.type.equals(other.type))
			return false;
		return true;
	}
}