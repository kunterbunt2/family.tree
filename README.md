# family.tree

**family.tree** is a command prompt tool that generates a genealogy as a pdf file using an excel file as input.
Unrelated parts of the tree will be displayed on separate pages.

## Every pdf document consists of

1. a header page with table of content
2. every page has the ISO page size as a watermark in the upper right corner.
3. every page has the calculated tree area as a watermark in the upper right corner.
4. a report page, including statistics and any existing errors. Possible errors are:<br>
   Error #001: [%d]%s %s is not visible. A person is visible if he is member of the family or has children with someone
   from the family.<br>
   Error #002. Page index null<br>
   Error #003. Person missing first name.<br>
   Error #004. Person missing last name.<br>
   Error #005. A %s member with unknown origins.<br>
   Error #006. [%d]%s %s is overlapping with [%d]%s %s.<br>
   Error #101. Column %s is missing.<br>
   Error #102. Unknown header '%s' at column '%s'.<br>

## There are several options that impact the overall visualization of a family tree.

| Command            | Default | Optional | Description                                                                                                                                                                       |
|--------------------|---------|----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| h                  | yes     | yes      | you can display the tree in horizontal mode, where children are displayed in order of birth from left to right below their parents.                                               |
| v                  | no      | yes      | you can display the tree in vertical mode, where children are displayed in order of birth from to to down to the right of their parents.                                          |
| ol                 | no      | yes      | you can use first and last names from the english column of the excel sheet or the original language column.                                                                      |
| c                  | no      | yes      | you can display the tree in compact mode, where birth and death dates are omitted to reduce the area needed for the trees.                                                        |
| follow_females     | no      | yes      | You can display children right to their father or mother, if both are applicable.                                                                                                 |
| exclude_spouse     | no      | yes      | You can chose to exclude the spouse to reduce the area needed for the trees.                                                                                                      |
| coordinates        | no      | yes      | Generate coordinates. This parameter is optional. Default is false.                                                                                                               |
| family_name        | no      | no       | Family name used to pic root of family. This parameter is optional.                                                                                                               |
| input              | no      | no       | Input excel file name. This parameter is not optional.                                                                                                                            |
| output             | no      | yes      | Output pdf file name. This parameter is optional. Default is input file name.                                                                                                     |
| output_decorations | no      | yes      | Output file name decorations. This parameter is optional.                                                                                                                         |
| split              | no      | yes      | Splits trees, that do not fit onto max_iso page sizes onto several pages. Parameter must be one of : top-down, bottom-up.This parameter is optional. Default is false.            |
| max_iso            | A4      | yes      | Maximum iso page size allowed. Any tree that does not fit will be split ont o several pages. Ignored if split option is nto specified. This parameter is optional. Default is A4. |
| min_iso            | A6      | yes      | Minimum iso page size allowed. Any page will be at least this size. This parameter is optional. Default is A6.                                                                    |
| grid               | no      | yes      | Generate a grid. This parameter is optional. Default is false.                                                                                                                    |
| cover_page         | no      | yes      | Generate a cover page. This parameter is optional. Default is false.                                                                                                              |

**Remark**: by default family.tree will try to optimize the needed area for a tree by shifting subtrees below (
horizontal mode) each other.

## Every person is displayed with

1. A box in blue color for males and pink color for women.
2. The person box border will be dotted to visualize that this is a copy of the original person. Copies are created for
   women that are displayed under their parents and additionally below their husbands and above their children. (
   configurable)
3. In the middle of the person box the first name, last name born date and death date are shown.
4. in case original language option is used, the first name and last name are taken from teh OL columns.
5. in the upper left corner, the Generation is displayed, starting with G0 for the root parents and incremented for
   every
   child generation.
6. In the upper right corner an asterisk \* is shown to show that this person box is a copy.
7. In the lower right corner the unique ID is displayed.
8. In the lower right corner the coordinates are displayed. Helpful to find a specific person box.

## Relations between persons are displayed using line connections.

1. Children have a line to their parents.
2. By default women are displayed below (horizontal mode) their male spouse connected to him with a dotted line.

**Remark**: you can search in the pdf for any of the text information to find a specific person.

# Installation

## How to install family.tree on Windows

family.tree is released as a msi installer for Windows platform. This installer is however not digitally signed. This
means that your Windows will warn you with a popup that the software could be harmful  
![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/installer-01.png)

You need to select More info to see the next popup  
![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/installer-02.png)  
Now you can select Run anyway to start the installer.

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

The tree is generated and displayed. It is also stored as a png file in the folder of the Excel file.

![alt tag](https://github.com/kunterbunt2/family.tree/blob/main/media/family.tree-03.png)

## Excel file specifications

Remark: There is an example Excel file in the examples folder.
The first sheet in teh Excel document is used to list all people in the genealogy.
Every record represents one person and the relations to other people within the same genealogy.

| Column          | Format         | Purpose                                                                                                         |
|:----------------|:---------------|:----------------------------------------------------------------------------------------------------------------|
| ID              | Formula        | Unique ID. Helpful to reference a person.                                                                       |
| Sex             | String         | The sex of the person. This is pure genetic aspect of the sex. Must be one of the terms 'Male' or 'Female'      |
| First Name      | String         | First name of the person in English language                                                                    |
| First Name (OL) | String         | First name of the person in the original language                                                               |
| Last Name       | String         | Last name of the person in English language                                                                     |
| Last Name (OL)  | String         | Last name of the person in the original language                                                                |
| Born            | Date           | Date the person was born. This date is used to sort members. If left empty, teh ID is used to sort members      |
| Died            | Date           | Date the person died.                                                                                           |
| Father          | Cell Reference | A reference to the First Name column of the record that represents the father of this person within this table. |
| Mother          | Cell Reference | A reference to the First Name column of the record that represents the mother of this person within this table. |
| Comment         | String         | Comments purely intended for the author of the table.                                                           |
| Comment (OL)    | String         | Comments in original language purely intended for the author of the table.                                      |

# Spechial Cases

**family.tree** detects if the members of the family are actually more than one family (not same ancestors), every
family is assigned a family letter starting with A to distinguish the families from each other.
