Datapack path

`(Datapackname)/data/(namespace)/fortunes/(fortunename).json`

Example json
```json
{
  "fortune": "Example Bad Fortune",
  "aura": "NEGATIVE",
  "weight": 5, // not required, defaulted to 10
  "effects": [
    {
      "effect": "minecraft:weakness",
      "duration": 40, // in seconds, not required, will default to 30
      "amplifier": 0 // level of effect, not required, level 1 by default
    },
    {
      "effect": "minecraft:mining_fatigue",
      "duration": 30,
      "amplifier": 0
    }
  ]
}

```
