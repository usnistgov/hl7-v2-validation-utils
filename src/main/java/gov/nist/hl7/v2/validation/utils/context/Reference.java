package gov.nist.hl7.v2.validation.utils.context;

public class Reference {
	String id;
	ReferenceType type;

	public Reference(String id, ReferenceType type) {
		this.id = id;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ReferenceType getType() {
		return type;
	}

	public void setType(ReferenceType type) {
		this.type = type;
	}


	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}

		Reference that = (Reference) o;

		if(! id.equals(that.id)) {
			return false;
		}
		return type == that.type;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + type.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "UnknownReference{" + "id='" + id + '\'' + ", type=" + type + '}';
	}
}
