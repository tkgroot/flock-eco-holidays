import React, {useEffect, useState} from "react"
import PropTypes from "prop-types"
import {
  Card,
  CardContent,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from "@material-ui/core"
import {PersonService} from "../../features/person/PersonService"

export function PersonSelector(props) {
  const {embedded, onChange, label, selectedItem} = props
  const [items, setItems] = useState([])
  const [selected, setSelected] = useState("")

  useEffect(() => {
    PersonService.getAll().then(res => setItems(res))
    setSelected(selectedItem)
  }, [])

  function handleChange(event) {
    // eslint-disable-next-line no-shadow
    const selected = event.target.value
    setSelected(selected)
    onChange(selected)
  }

  function renderMenuItem(item, key) {
    return (
      <MenuItem key={`person-selector-menu-item-${key}`} value={item.code}>
        {item.firstname} {item.lastname}
      </MenuItem>
    )
  }

  const selectInput = (
    <FormControl fullWidth>
      <InputLabel shrink>{label}</InputLabel>
      <Select value={selected} displayEmpty onChange={handleChange}>
        <MenuItem value="">
          <em>None</em>
        </MenuItem>
        {items.map(renderMenuItem)}
      </Select>
    </FormControl>
  )

  return embedded ? (
    <div>{selectInput}</div>
  ) : (
    <Card>
      <CardContent>{selectInput}</CardContent>
    </Card>
  )
}

PersonSelector.propTypes = {
  embedded: PropTypes.bool,
  onChange: PropTypes.func.isRequired,
  label: PropTypes.string,
  selectedItem: PropTypes.string,
}

PersonSelector.defaultProps = {
  embedded: false,
  selectedItem: "",
  label: "Select Person",
}
