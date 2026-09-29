0.1.2+1.20.1

- replaces the equipped-backpack `BackpackDeathHelper` bridge with direct native TB placement invokers
- avoids the incompatible Graves 1.0.0 handoff
- preserves normal-inventory Graves handling
- automated validation passed; manual game validation pending

0.1.1+1.20.1

- fixes Fabric main entrypoint accessibility/bootstrap crash
- no death-bridge behavior changes
- manual launch validation passed
- unsuitable for equipped-backpack survival use with Graves 1.0.0

0.1.0+1.20.1 — broken/obsolete for runtime testing

- Phase B first implementation
- automated validation passed
- Fabric entrypoint constructor was private and caused bootstrap crash
