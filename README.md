# family.tree
family.tree is a command promp tool that generates a genealogy as a png image using an excel file as input.

# Installation
## How to install family.tree on Windows
family.tree is released as a msi installer for Windows platform. This installer is however not digitally signed. This means that your Windows will warn you with a popup that the software could be harmful  
![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/installer-01.png)  

You need to select More info to see the next popup  
![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/installer-02.png)  
Now you can seelect Run anyway to start the installer.

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/installer-03.png)  

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/installer-04.png)  

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/installer-05.png)  

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/installer-06.png)  

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/installer-07.png)  

#How To Use family.tree
family.tree consists of only one screen

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/family.tree-01.png)  

You can configure the tree
1. to be a horizontal tree (work in progress)
2. to be a vertical tree
3. children to be displayed right to their father or mother, if both are applicable
4. exclude spouse (to make hte tree smaller)
5. include the name in original language if existing

Then select your excel file by pushing the 'Select Excel File' button

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/family.tree-02.png)  

The tree is generated and dislayed. It is also stored as a png file in the folder of the Excel file.

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/family.tree-03.png)  


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

# Issues
PdfBox PDExtendedGraphicsState.setLineWidth has no impact on setLineDashPattern
