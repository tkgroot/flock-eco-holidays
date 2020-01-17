import {responseValidation, ResourceClient} from "../utils/ResourceClient"
import {toHolidayForm} from "../features/holiday/schema"

const path = "/api/holidays"
const client = ResourceClient(path)

const findAllByPersonCode = personCode => {
  return fetch(`${path}?personCode=${personCode}`)
    .then(responseValidation)
    .then(data => data.map(toHolidayForm))
}
const findAll = () => {
  return fetch(`/api/holidays`)
    .then(responseValidation)
    .then(data => data.map(toHolidayForm))
}

const findByCode = holidayCode => {
  return fetch(`${path}/${holidayCode}`)
    .then(responseValidation)
    .then(toHolidayForm)
}

function post(holidayForm) {
  return client.post(toHolidayForm(holidayForm))
}

function put(id, holidayForm) {
  return client.put(id, toHolidayForm(holidayForm))
}

export const HolidayClient = {
  ...client,
  findAll,
  findByCode,
  findAllByPersonCode,
  post,
  put,
}
