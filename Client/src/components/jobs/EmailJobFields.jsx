const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const validateEmailList = (value, { required = false, fieldName }) => {
    const emails = (value || "")
        .split(",")
        .map((e) => e.trim())
        .filter(Boolean);

    if (required && emails.length === 0) {
        return `${fieldName} field can't be empty`;
    }

    const invalid = emails.find((email) => !EMAIL_REGEX.test(email));
    if (invalid) {
        return `Invalid email address in ${fieldName}: "${invalid}"`;
    }

    return true;
};

export default function EmailJobFields({ register, errors }) {
    return (
        <div className="space-y-4">
            {/* Send To */}
            <fieldset className="fieldset">
                <legend className="fieldset-legend">
                    Send To <span className="text-error">*</span>
                </legend>
                <input
                    type="text"
                    placeholder="alice@example.com, bob@example.com"
                    className={`input w-full ${errors.sendTo ? "input-error" : ""}`}
                    {...register("sendTo", {
                        validate: (v) =>
                            validateEmailList(v, { required: true, fieldName: "Send to" }),
                    })}
                />
                <p className="label text-xs opacity-70">Separate multiple emails with commas</p>
                {errors.sendTo && <p className="label text-error">{errors.sendTo.message}</p>}
            </fieldset>

            {/* CC & BCC */}
            <div className="grid gap-4 sm:grid-cols-2">
                <fieldset className="fieldset">
                    <legend className="fieldset-legend">CC (Optional)</legend>
                    <input
                        type="text"
                        placeholder="cc1@example.com, cc2@example.com"
                        className={`input w-full ${errors.cc ? "input-error" : ""}`}
                        {...register("cc", {
                            validate: (v) =>
                                validateEmailList(v, { required: false, fieldName: "CC" }),
                        })}
                    />
                    {errors.cc && <p className="label text-error">{errors.cc.message}</p>}
                </fieldset>

                <fieldset className="fieldset">
                    <legend className="fieldset-legend">BCC (Optional)</legend>
                    <input
                        type="text"
                        placeholder="bcc@example.com"
                        className={`input w-full ${errors.bcc ? "input-error" : ""}`}
                        {...register("bcc", {
                            validate: (v) =>
                                validateEmailList(v, { required: false, fieldName: "BCC" }),
                        })}
                    />
                    {errors.bcc && <p className="label text-error">{errors.bcc.message}</p>}
                </fieldset>
            </div>

            {/* Subject */}
            <fieldset className="fieldset">
                <legend className="fieldset-legend">
                    Subject <span className="text-error">*</span>
                </legend>
                <input
                    type="text"
                    placeholder="Welcome to FlowForge"
                    className={`input w-full ${errors.subject ? "input-error" : ""}`}
                    {...register("subject", {
                        required: "Subject field can't be empty",
                        validate: (v) => v.trim().length > 0 || "Subject field can't be empty",
                    })}
                />
                {errors.subject && <p className="label text-error">{errors.subject.message}</p>}
            </fieldset>

            {/* Body */}
            <fieldset className="fieldset">
                <legend className="fieldset-legend">
                    Body <span className="text-error">*</span>
                </legend>
                <textarea
                    rows={5}
                    placeholder="Write your email content here..."
                    className={`textarea w-full ${errors.body ? "textarea-error" : ""}`}
                    {...register("body", {
                        required: "Body field can't be empty",
                        validate: (v) => v.trim().length > 0 || "Body field can't be empty",
                    })}
                />
                {errors.body && <p className="label text-error">{errors.body.message}</p>}
            </fieldset>
        </div>
    );
}