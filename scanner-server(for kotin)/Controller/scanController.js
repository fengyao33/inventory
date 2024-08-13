const Scan = require('../Schema/ScanSchema');

async function findlocation(req, res){
    const { DateTime, Location, PalletNo } = req.body;
    const searchDate = DateTime.split(' ')[0]; 
    try {
        const scans = await Scan.find({
            DateTime: {
                $gte: new Date(searchDate + 'T00:00:00.000Z'), 
                $lt: new Date(new Date(searchDate + 'T23:59:59.999Z').getTime() + 1) 
            },
            Location: Location,
            PalletNo: PalletNo
        });

        if (scans.length > 0) {
            res.status(200).json({
                success: true,
                has: true,
                time: scans.length.toString()
            }); 
        } else {
            res.status(200).json({ success: true, has: false });
        }

    } catch (err) {
        console.error('[0001] - Error finding scans:', err);
        res.status(500).json({ success: false, err: err });
    }
}

async function createScan(req, res) {

    try {
        const { DateTime, Location, PalletNo, PartNo, FullPallet, SQoc } = req.body;

        const searchDate = DateTime.split(' ')[0]; 

        let ScanTime = 1

        if(PalletNo !== 'X'){
            const check = await Scan.find({
                DateTime: {
                    $gte: new Date(searchDate + 'T00:00:00.000Z'), 
                    $lt: new Date(new Date(searchDate + 'T23:59:59.999Z').getTime() + 1) 
                },
                Location: Location,
                PalletNo: PalletNo
            });
    
            if(check.length > 0 ){
                ScanTime += check.length
            }
        }

        if (PalletNo === 'X') {
            await Scan.deleteOne({
                Location: Location,
                PalletNo: PalletNo,
                PartNo: PartNo,
                DateTime: {
                    $gte: new Date(searchDate + 'T00:00:00.000Z'), 
                    $lt: new Date(new Date(searchDate + 'T23:59:59.999Z').getTime() + 1) 
                },
            })

        }
        const scan = new Scan({ DateTime, Location, PalletNo, PartNo, FullPallet, ScanTime, SQoc });

        await scan.save();
        res.status(200).json({ success: true, scan });
    } catch (err) {
        console.error('[0002] - Error create scans:', err);
        res.status(500).json({ error: err.message });
    }
}


async function getAllScan(req, res) {
    try {
        const data = await Scan.find();
        res.status(200).json({ success: true, data });
    } catch (err) {
        console.error('[0003] - Error getAllScan:', err);
        res.status(500).json({ error: err.message });
    }
}

module.exports = {
  createScan,
  findlocation,
  getAllScan
};

