# family.tree

family.tree is a command prompt tool that generates a genealogy as a pdf file using an excel file as input.
Unrelated parts of the tree will be displayed on seperate pages.

Every pdf document consists of

1. a readme page
2. every page has the ISO page size as a watermark in the upper right corner.
3. every page has the calculated tree area as a watermark in the upper right corner.
4. an error page, in case there are any errors. Possble errors are:<br>
   Error #001: [%d]%s %s is not visible. A person is visible if he is member of the family or has children with someone
   from the family.<br>
   Error #002. Page index null<br>
   Error #003. Person missing first name.<br>
   Error #004. Person missing last name.<br>
   Error #005. A %s member with unknown origins.<br>
   Error #006. [%d]%s %s is overlapping with [%d]%s %s.<br>
   Error #101. Column %s is missing.<br>
   Error #102. Unknown header '%s' at column '%s'.<br>

There are several options that impact the overall visualization of a family tree.

1. you can display the tree in horizontal mode, where children are displayed in order of birth from left to right below
   their parents.
2. you can display the tree in vertical mode, where children are displayed in order of birth from to to down to the
   right of their parents.
3. you can use first and last names from the english column of the excel sheet or the original langauge column.
4. you can display the tree in compact mode, where birth and death dates are omitted to reduce the area needed for the
   trees.
5. You can display children right to their father or mother, if both are applicable.
6. You can chose to exclude the spouse to reduce the area needed for the trees.
7. By default family.tree will try to optimize the needed area for a tree by shifting subtrees below (horizontal mode)
   each other.

Every person is displayes with

1. A box in blue color for males and pink color for women.
2. The person box border will be dotted to visualize that this is a copy of the original person. Copies are created for
   women that are diplayed under their parents and additionally below their husbands and above their children. (
   configurable)
3. In the middle of the person box the first name, last name born date and death date are shown.
4. in case original language option is used, the first name and last name are taken from teh OL columns.
5. in the upper left corner, the Genration is displayed, starting with G0 for the root parents and incremented for every
   child genration.
6. In the upper right corner an asterix \* is shown to show that thsi person box is a copy.
7. In the lower right corner the unique ID is displayed.
8. In the lower right corner the coordinates are displyed. Helpfull to find a specific person box.

Relations between persons are displyed using line connections.

1. Children have a line to their parents.
2. By default women are displayed below (horizontal mode) their male spouse connected to him with a dotted line.

Remark: you can search in the pdf for any of the text information to find a specific person.

# Installation

## How to install family.tree on Windows

family.tree is released as a msi installer for Windows platform. This installer is however not digitally signed. This
means that your Windows will warn you with a popup that the software could be harmful  
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

1. to be a horizontal tree (children are displayed in order of birth from left to right below their parents)
2. to be a vertical tree (children are displayed in order of birth from to to down to the right of their parents)
3. children to be displayed right to their father or mother, if both are applicable
4. exclude spouse (to make the tree smaller)
5. include the name in original language if existing
6. compact mode (birth and death birth dates are omitted to allow smaller trees)

Then select your excel file by pushing the 'Select Excel File' button

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/family.tree-02.png)

The tree is generated and dislayed. It is also stored as a png file in the folder of the Excel file.

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/family.tree-03.png)

## Excel file specifications

Remark: There is an example Excel file in the examples folder.
The first sheet in teh Excel document isused to list all people in the genealogy.
Every record represents one person and the relations to other peopele within the same genealogy.

| Column          | Format         | Purpose                                                                                                         |
|:----------------|:---------------|:----------------------------------------------------------------------------------------------------------------|
| ID              | Formular       | Unique ID. Helpfull to reference a person.                                                                      |
| Sex             | String         | The sex of the person. This is pure genetical aspect of the sex. Must be one of the terms 'Male' or 'Female'    |
| First Name      | String         | First name of the person in English langugae                                                                    |
| First Name (OL) | String         | First name of the person in the origianl language                                                               |
| Last Name       | String         | Last name of the person in English langugae                                                                     |
| Last Name (OL)  | String         | Last name of the person in the origianl language                                                                |
| Born            | Date           | Date the person was born. This date is used to sort members. If left empty, teh ID is used to sort members      |
| Died            | Date           | Date the person died.                                                                                           |
| Father          | Cell Reference | A reference to the First Name column of the record that represents the father of this person within this table. |
| Mother          | Cell Reference | A reference to the First Name column of the record that represents the mother of this person within this table. |
| Comment         | String         | Comments purly intended for the author of the table.                                                            |
| Comment (OL)    | String         | Comments in original language purly intended for the author of the table.                                       |

#Spechial Cases
family.tree detects if the members of the family are actually more than one family (not same ansesters), every family is
assigned a family letter starting with A to distinguish the families from each other.

# Missing tests

1. every person with a father must have a mother and vice versa
2. every person has to have a first name

# Issues

PdfBox PDExtendedGraphicsState.setLineWidth has no impact on setLineDashPattern
