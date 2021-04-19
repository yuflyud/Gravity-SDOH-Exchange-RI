export type Patient = {
	address: string | null,
	age: number | null,
	dob: string | null,
	gender: string | null,
	id: string | null,
	name: string | null
	//todo: keys below we don't have in our api yet
	language: string | null,
	phone: string | null,
	email: string | null,
	employmentStatus: string | null,
	race: string | null,
	ethnicity: string | null,
	educationLevel: string | null,
	maritalStatus: string | null,
	insurance: string | null
};

export type User = {
	id: string | null,
	name: string | null,
	userType: string | null
};

export type ContextResponse = {
	patient: Patient,
	user: User
}

export type Organization = {
	errors: string[],
	name: string,
	organizationId: string,
	type: "CBO" | "CBRO"
}

export type ServiceRequest = {
	//todo: on be it's enum right now
	category: string,
	details: string,
	errors: string[],
	//todo: on be it's enum right now
	request: string,
	serviceRequestId: string,
	status: ServiceRequestStatus
}

export type ServiceRequestStatus = "ACTIVE" | "COMPLETED" | "DRAFT" | "ENTEREDINERROR" | "NULL" | "ONHOLD" | "REVOKED" | "UNKNOWN"

export type TaskResponse = {
	createdAt: string,
	errors: string[],
	lastModified: string | null,
	organization: Organization | null,
	outcome: string | null,
	serviceRequest: ServiceRequest,
	status: TaskStatus,
	taskId: string,
	type: TaskType
}

export type TaskStatus = "ACCEPTED" | "CANCELLED" | "COMPLETED" | "DRAFT" | "ENTEREDINERROR" | "FAILED" | "INPROGRESS" | "NULL" | "ONHOLD" | "READY" | "RECEIVED" | "REJECTED" | "REQUESTED"

export type TaskType = "ABORT" | "APPROVE" | "CHANGE" | "FULFILL" | "NULL" | "REPLACE" | "RESUME" | "SUSPEND"
