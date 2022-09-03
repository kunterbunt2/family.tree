# family.tree
family.tree is a command promp tool that generates a genealogy as a png image using an excel file as input.
## Excel file specifications
Remark: There is an example Excel file in the examples folder.
The first sheet in teh Excel document isused to list all people in the genealogy.
Every record represents one person and the relations to other peopele within the same genealogy.

| Column |Format|Purpose |
|:----------|:---|:-------------|
|ID|Formular|Unique ID. Helpfull to reference a person.|
|Sex|String|The sex of the person. This is pure genetical aspect of the sex. Must be one of the terms 'Male' or 'Female'|
|First Name|String|First name of the person in English langugae|
|First Name (OL)|String|First name of the person in the origianl language|
|Last Name|String|Last name of the person in English langugae|
|Last Name (OL)|String|Last name of the person in the origianl language|
|Born|Date|Date the person was born. This date is used to sort members. If left empty, teh ID is used to sort members|
|Died|Date|Date the person died.|
|Father|Cell Reference|A reference to the First Name column of the record that represents the father of this person within this table.|
|Mother|Cell Reference|A reference to the First Name column of the record that represents the mother of this person within this table.|
|Comment|String|Comments purly intended for the author of the table.|
|Comment (OL)|String|Comments in original language purly intended for the author of the table.|


# Missing tests
1. every person with a father must have a mother and vice versa
2. every person has to have a first name
