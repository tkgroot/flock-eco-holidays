import moment from "moment"

const internalize = it => ({
  ...it,
  period: {
    ...it.period,
    from: moment(it.period.from, "YYYY-MM-DD"),
    to: moment(it.period.to, "YYYY-MM-DD"),
    days: it.period.days.map(day => ({
      ...day,
      date: moment(day.date, "YYYY-MM-DD"),
    })),
  },
})
function findAllByUserCode(userCode) {
  return fetch(`/api/holidays?userCode=${userCode}`)
    .then(res => {
      if (res.status === 200) {
        return res.json()
      }
      throw res.json()
    })
    .then(data => data.map(internalize))
}

function findByCode(code) {
  return fetch(`/api/holidays/${code}`)
    .then(res => {
      if (res.status === 200) {
        return res.json()
      }
      throw res.json()
    })
    .then(internalize)
}

function findAll() {
  return fetch(`/api/holidays`)
    .then(res => {
      if (res.status === 200) {
        return res.json()
      }
      throw res.json()
    })
    .then(data => data.map(internalize))
}

function postHoliday(holiday) {
  const opts = {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(holiday),
  }
  return fetch(`/api/holidays`, opts).then(res => res.json())
}

function putHoliday(id, holiday) {
  const opts = {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(holiday),
  }
  return fetch(`/api/holidays/${id}`, opts).then(res => res.json())
}

function deleteHoliday(id) {
  const opts = {
    method: "DELETE",
  }
  return fetch(`/api/holidays/${id}`, opts).then(res => res.json())
}

function getAllUsers() {
  return fetch(`/api/users/`).then(res => {
    if (res.status === 200) {
      return res.json()
    }
    throw res.json()
  })
}

function getMe() {
  return fetch(`/api/users/me`).then(res => {
    if (res.ok) {
      return res.json()
    }
    throw res.json()
  })
}

function getUserById(id) {
  return fetch(`/api/user/${id}`).then(res => {
    if (res.status === 200) {
      return res.json()
    }
    throw res.json()
  })
}

function getSummary(filter) {
  const typeFilter = filter.length > 0 ? `?type=${filter}` : filter

  return fetch(`/api/holidays/summary${typeFilter}`).then(res => {
    if (res.status === 200) {
      return res.json()
    }
    throw res.json()
  })
}

export default {
  findAll,
  findByCode,
  findAllByUserCode,
  postHoliday,
  putHoliday,
  deleteHoliday,
  getAllUsers,
  getMe,
  getUserById,
  getSummary,
}
