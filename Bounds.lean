/-! Bounds for the recurrence z_i at the end of our CPII proof -/

def e : Float :=
  .exp 1

def dumbBound (i n : Nat) : Float :=
  n.toFloat * e / (e - 1) * (1 / i.toFloat)

def i₀ (n : Nat) : Nat :=
  let n := n.toFloat
  (Float.ceil (n / n.log)).toUInt64.toNat + 1

def c₀ : Float := 1

def combinedBound (i n : Nat) : Float :=
  let i₀ := i₀ n
  if i > i₀ then
    Nat.fold i₀ (init := dumbBound i₀ n)
      fun _ _ => next n
  else dumbBound i n
where next (n : Nat) (x : Float) : Float :=
  let n := n.toFloat
  x - (1 - 1 / e) * (n.log / (c₀ * n)) * (max (x - 1 / n - 1) 0)^2 + n.log^4/n^3

def main : List String → IO Unit
  | [nStr] => do
    let some nMax := nStr.toNat? | throw <| .userError s!"Couldn't parse `{nStr}`"
    for n in [2 : nMax + 1] do
      let i := 2 * i₀ n
      let bound := combinedBound i n
      println! s!"{n},{bound}"
  | args => throw <| .userError s!"Arguments `{args}`"
