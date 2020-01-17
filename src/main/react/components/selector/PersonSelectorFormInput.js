import React from "react"
import {Field} from "formik"
import {PersonSelector} from "./PersonSelector"

const fieldName = "personCode"

function PersonSelectorFormInput() {
  return (
    <Field id={fieldName} name={fieldName} as="select">
      {({field: {value}, form: {setFieldValue}}) => (
        <PersonSelector
          embedded
          selectedItem={value}
          onChange={personCode => setFieldValue(fieldName, personCode)}
        />
      )}
    </Field>
  )
}

PersonSelectorFormInput.propTypes = {}

export {PersonSelectorFormInput}
