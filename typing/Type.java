package typing;

public enum Type {
    INT_TYPE {
		public String toString() {
            return "int";
        }
	},
    REAL_TYPE {
		public String toString() {
			return "real";
		}
	},
    BOOL_TYPE {
		public String toString() {
            return "bool";
        }
	},
    STR_TYPE {
		public String toString() {
            return "string";
        }
	},
	NO_TYPE { // Indica um erro de tipos.
		public String toString() {
            return "no_type";
        }
	};
}
