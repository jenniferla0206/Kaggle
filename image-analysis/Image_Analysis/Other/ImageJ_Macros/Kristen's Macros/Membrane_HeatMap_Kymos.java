//FUNICTIONS

//Returns the maximum of the array
function maxOfArray(array) {
    min=0;
    for (a=0; a<lengthOf(array); a++) {
        min=minOf(array[a], min);
    }
    max=min;
    for (a=0; a<lengthOf(array); a++) {
        max=maxOf(array[a], max);
    }
    return max;
}

//Returns the minimum of the array
function minOfArray(array) {
    max=0;
    for (a=0; a<lengthOf(array); a++) {
        max=maxOf(array[a], max);
    }
    min=max;
    for (a=0; a<lengthOf(array); a++) {
        min=minOf(array[a], min);
    }
    return min;
}

//Returns the minimum value of the array that is greater than 0
function minNonZeroOfArray(array) {
    max=0;
    for (a=0; a<lengthOf(array); a++) {
        max=maxOf(array[a], max);
    }
    min=max;
    for (a=0; a<lengthOf(array); a++) {
        if (array[a]>0) {
        	min=minOf(array[a], min);
        } else {
        	min=minOf(1000, min);
        }
    }
    return min;
}

//Returns the indices at which a value occurs within an array 
function indexOfArray(array) { 
    count=0; 	
    for (i=0; i<lengthOf(array); i++) { 
        if (array[i]==value) { 
            count++; 
        } 
    } 
    if (count>0) { 
        //indices=newArray(count); 
        count=0; 
        for (i=0; i<lengthOf(array); i++) { 
            if (array[i]==value) { 
                indices=i; 
                count++; 
            } 
        } 
		return indices;  
	}
}

//Prints the array in a human-readable form
function printArray(array) {
    string="";
    for (i=0; i<lengthOf(array); i++) {
        if (i==0) {
            string=string+array[i];
        } else {
            string=string+", "+array[i];
        }
    }
    print(string);
}

//Closes all images
function closeAllImages()	{
 	while (nImages>0) {
		selectImage(nImages);
		close(); 
	}
}


roiManager("reset");

msg="Choose the x,y coordinate of the bud site by drawing a circle around the emerging bud in the first frame visible.";
waitForUser(msg + "\n" + "When finished, press OK");
budEmerge=getSliceNumber();
selectWindow("Duplicate_GFP");
roiManager("Add");
List.setMeasurements;
//print(List.getList);
budX=List.getValue("X");
budY=List.getValue("Y");

waitForUser("Choose the current cell folder");
tmp=getDirectory("Choose a Directory");
IJ.log(tmp);

snakeGFPDir=tmp+"GFP_Straight_Snakes"+File.separator;
File.makeDirectory(snakeGFPDir);
  if (!File.exists(snakeGFPDir))
      exit("Unable to create directory");
  print("");
  print(snakeGFPDir);

snakeREDDir=tmp+"dsRED_Straight_Snakes"+File.separator;
File.makeDirectory(snakeREDDir);
  if (!File.exists(snakeREDDir))
      exit("Unable to create directory");
  print("");
  print(snakeREDDir);
  
//waitForUser("Choose the directory containing your Snakes file.");
dir=tmp+"Current Snake"+File.separator;
IJ.log(dir);
traces=getFileList(dir);
selectWindow("Duplicate_GFP");
f=nSlices();
r=nResults();
cellNumber=newArray(f+1);
numRows=0;
distRows=0;

maxInt=newArray(f+1);
maxNormInt=newArray(f+1);
maxCoord=newArray(f+1);
minInt=newArray(f+1);
minCoord=newArray(f+1);

setBatchMode(true);

// IJ.log("budX="+budX);
// IJ.log("budY="+budY);

//IMPORT THE MOTHER CELL SNAKES

for (n=1; n<=f; n++) {
	setSlice(n);
	cellNumber[n]=n;
	//IJ.log("n=" + n);
	badPts=0;
			
		for (i=0; i<traces.length; i++) {
			//IJ.log("traces=" + traces.length);
			string=File.openAsString(dir + traces[i]);
			rows=split(string, "\n");
			//IJ.log("#rows=" + rows.length);
			counter=0;
		
			for(j=0; j<rows.length; j++){
				column=split(rows[j], "\t");
			
				if (column[0]==n) {
					counter++;		
				}
			}
			
			//IJ.log("numRows=" + numRows);
			//IJ.log("counter=" + counter);
			x=newArray(rows.length);
			y=newArray(rows.length);
			distTmp=newArray(rows.length);
			xcoordTmp=newArray(counter);
			ycoordTmp=newArray(counter);
			
			//ADD THE SNAKE ROIS, DEFINE THE CENTER, AND DRAW A LINE FROM BUD TO OPP SIDE
		
			for(j=numRows; j<rows.length; j++){
				column=split(rows[j], "\t");
				
				if (column[0]==n) { 
					x[j]=parseFloat(column[2]);
					y[j]=parseFloat(column[3]);
					numRows++;
					//IJ.log("numRows="+numRows);
 				}
 			}
 				
 			if (n>1) {
 				start=numRows-counter;
 			} else {
 				start=0;
 			}
 				
 			//IJ.log("start=" + start);
 			xcoordTmp=Array.slice(x, start, numRows);
			ycoordTmp=Array.slice(y, start, numRows);
			//IJ.log("distTmp="+distTmp[j]);
 			
 			makeSelection("polyline", xcoordTmp, ycoordTmp);
			run("Interpolate", "interval=1");
			run("Line to Area");
			//roiManager("Add");
			List.setMeasurements;
			//print(List.getList);
			cellX=List.getValue("X");
			cellY=List.getValue("Y");
			//IJ.log("cellX="+cellX+"\n"+"cellY="+cellY);
			
			diffX=cellX-budX;
			diffY=cellY-budY;
			m=(diffY)/(diffX);
			//IJ.log("m="+m);
			b=budY-m*budX;
			//IJ.log("b="+b);
			
			//waitForUser("check"); //Debugging
			
			//FIND THE DISTANCES FROM THE LINE TO POINTS OF THE SNAKE THAT ARE OPP THE BUD
			
 			for(j=distRows; j<rows.length; j++){
				column=split(rows[j], "\t");
				dist=newArray(counter);
				
				if (column[0]==n) { 
					xPt=parseFloat(column[2]);
 					yPt=parseFloat(column[3]);
 					distRows++;
 					
 					if (cellX<budX){
 						
 						if (xPt<=cellX){
 							top=yPt-m*xPt-b;
 							//IJ.log("top="+top);
 							bottom=m*m+1;
 							//IJ.log("bottom="+bottom);
 							distTmp[j]=(abs(top))/(sqrt(bottom));
							//IJ.log("numRows Again="+numRows);
						} else {
 						//IJ.log("xPt="+xPt+"\t"+"yPt="+yPt+"\n"+"xPt on the budding half of the cell");
 						badPts++;
 						//IJ.log("badPts="+badPts);
 						}
 					}
 					
 					if (cellX>budX){
 						
 						if (xPt>=cellX){
 							top=yPt-m*xPt-b;
 							//IJ.log("top="+top);
 							bottom=m*m+1;
 							//IJ.log("bottom="+bottom);
 							distTmp[j]=(abs(top))/(sqrt(bottom));
							//IJ.log("numRows Again="+numRows);
						} else {
 						//IJ.log("xPt="+xPt+"\t"+"yPt="+yPt+"\n"+"xPt on the budding half of the cell");
 						badPts++;
 						//IJ.log("badPts="+badPts);
 						}
 					}
 				}
 			}
 				
 			dist=Array.slice(distTmp, start, numRows);
 			
 			//REARRANGE XCOORDTMP AND YCOORDTMP ARRAYS SO THAT THE START AND END ARE AT THE POINT CLOSEST TO THE LINE OPP THE BUD 
 			
 			//IJ.log("dist=");
 			//Array.print(dist);
 			//printArray(dist);
			value=minNonZeroOfArray(dist);
 			indexMin=indexOfArray(dist);
 			end=indexMin-1;
 			//IJ.log("min of dist="+value);
 			//IJ.log("index of min="+indexMin);
 			xSplit1=Array.slice(xcoordTmp, indexMin, counter);
 			xSplit2=Array.slice(xcoordTmp, 0, end);
 			xcoord=Array.concat(xSplit1, xSplit2);
 			//printArray(xcoord);
 			ySplit1=Array.slice(ycoordTmp, indexMin, counter);
 			ySplit2=Array.slice(ycoordTmp, 0, end);
 			ycoord=Array.concat(ySplit1, ySplit2);
 			//printArray(ycoord);
 			
 			//waitForUser("check"); //Debugging
 			
 			//ADD THE REORGANIZED ARRAYS AS A POLYLINE SELECTION AND PROCESS THE PIXEL INTENSITY ACROSS THE LINE
 			
 			makeSelection("polyline", xcoord, ycoord);
 			roiManager("Add");	
			title=getSliceNumber();
			//IJ.log("title="+title);
			run("Interpolate", "interval=1");
			run("Straighten...", "line=6");
			selectWindow("Duplicate_GFP-1");
			w=getWidth;
			//IJ.log(w);
			if (w<100) {
				
				run("Size...", "width=100 height=6 average interpolation=Bilinear");
			}	
			saveAs("Tiff", snakeGFPDir+title);
			selectWindow("Duplicate_GFP");
		}
	}
	
selectWindow("Duplicate_dsRED");
f=nSlices();
//IJ.log("#frames=" + f);
	//f for frame
r=nResults();
cellNumber=newArray(f+1);
numRows=0;

// maxInt=newArray(f+1);
// maxNormInt=newArray(f+1);
// maxCoord=newArray(f+1);
// minInt=newArray(f+1);
// minCoord=newArray(f+1);
				
for (n=1; n<=f; n++) {
	setSlice(n);
	cellNumber[n]=n;
	//IJ.log("n=" + n);
	
	roiManager("select",n);
	//makeSelection("polyline", xcoord, ycoord);
 	//roiManager("Add");	
	title=getSliceNumber();
	//IJ.log("title="+title);
	run("Interpolate", "interval=1");
	run("Straighten...", "line=6");
	selectWindow("Duplicate_dsRED-1");
	w=getWidth;
	
	if (w<100) {
		run("Size...", "width=100 height=6 average interpolation=Bilinear");
	}
	
	saveAs("Tiff", snakeREDDir+title);
	selectWindow("Duplicate_dsRED");
}

closeAllImages();

stack=tmp+"GFP_Straight_Snakes";
run("Image Sequence...", "open=stack number=121 starting=1 increment=1 scale=100 file=[] or=[] sort");
run("Make Montage...", "columns=1 rows=121 scale=1 first=1 last=121 increment=1 border=0 font=12");
saveAs("Tiff",tmp+"GFP_Montage");
stack=tmp+"dsRED_Straight_Snakes";
run("Image Sequence...", "open=stack number=121 starting=1 increment=1 scale=100 file=[] or=[] sort");
run("Make Montage...", "columns=1 rows=121 scale=1 first=1 last=121 increment=1 border=0 font=12");
saveAs("Tiff",tmp+"dsRED_Montage");
roiManager("Save",tmp+"ROIs.zip");

jpeg=tmp+"JPEG"+File.separator;
File.makeDirectory(jpeg);
  if (!File.exists(jpeg))
      exit("Unable to create directory");

selectWindow("dsRED_Montage.tif");
run("Duplicate...", "title=dsRED_Montage-1");

selectWindow("dsRED_Montage-1");
run("Smooth");
setMinAndMax(1, 25);
//call("ij.ImagePlus.setDefault16bitRange", 0);
run("Fire");
selectWindow("dsRED_Montage-1");
run("Flatten");
//waitForUser("debugging");
//selectWindow("dsRED_Montage-2");
saveAs("Tiff", tmp+"dsRED_Montage_HM");
selectWindow("dsRED_Montage_HM.tif");
run("Duplicate...", "title=dsRED_Montage_HM");
saveAs("Jpeg", jpeg+"dsRED_Montage_HM");

selectWindow("GFP_Montage.tif");
run("Duplicate...", "title=GFP_Montage-1");
selectWindow("GFP_Montage-1");
setAutoThreshold("Default dark");
run("Create Selection");
run("Enlarge...", "enlarge=1");
selectWindow("dsRED_Montage_HM.tif");
run("Restore Selection");
setColor("green");
fill();
saveAs("Tiff", tmp+"dsRED_Montage_HM_Centers");
saveAs("Jpeg", jpeg+"dsRED_Montage_HM_Centers");

setBatchMode(false);
