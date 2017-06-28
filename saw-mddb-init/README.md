# Introduction

Utility to load sample semantic metadata into Metadata Service

# Types of files

The sample semantic metadata contains of the following types of files:

- Semantic nodes: named according to the `*_semantic_node.json`
  pattern.  Semantic nodes are used as templates for creating
  analyses.
- Data objects: named according to the `*_data_object.json` pattern.
  Data objects describe data in the data lake, for example Parquet
  files.

# Loading for the first time

If loading data for the first time, change the verbs from "update" to
"create".  Save the output from the execution and update the sample
JSON files with the new IDs (both semantic nodes and the data objects,
including references to data objects from semantic nodes).
