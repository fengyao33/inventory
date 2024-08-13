const mongoose = require('mongoose');

const ScanSchema = new mongoose.Schema({
  DateTime: { type: Date, required: true },
  Location: { type: String, required: true },
  PalletNo: { type: String, required: true },
  PartNo: { type: String, required: true },
  FullPallet: { type: Boolean, required: true },
  ScanTime: { type: Number },
  SQoc: { type: String }
},{ versionKey: false });

// ScanSchema.index({ _id: 1 }, { unique: true });

// ScanSchema.pre('save', function(next) {
//   if (!this._id) {
//     this._id = uuidv4(); 
//   }
//   next();
// });


const Scan = mongoose.model('Scan', ScanSchema);
module.exports = Scan;
