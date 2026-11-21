import EmailJobFields from "./EmailJobFields";

const parseEmailList = (value) => {
    if (!value || !value.trim()) return [];
    return value
        .split(",")
        .map((email) => email.trim())
        .filter(Boolean);
};

export const JOB_TYPES = {
    EMAIL: {
        label: "Email Job",
        description: "Send transactional or bulk emails asynchronously via Kafka workers.",
        component: EmailJobFields,
        defaultValues: {
            sendTo: "",
            cc: "",
            bcc: "",
            subject: "",
            body: "",
        },
        buildPayload: (data) => ({
            type: "EMAIL",
            sendTo: parseEmailList(data.sendTo),
            cc: parseEmailList(data.cc),
            bcc: parseEmailList(data.bcc),
            subject: data.subject.trim(),
            body: data.body.trim(),
        }),
    },

    // Future job types plug in right here:
    // WEBHOOK: { label: "Webhook Job", component: WebhookJobFields, ... }
};