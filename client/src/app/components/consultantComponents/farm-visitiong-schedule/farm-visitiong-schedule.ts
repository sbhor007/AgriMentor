import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { FarmVisit } from '../../../services/farmVisit/farm-visit';

// Custom validator to ensure date is in the future
function futureDateValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) {
    return null; // Don't validate empty values (let required validator handle that)
  }

  const selectedDate = new Date(control.value);
  const today = new Date();
  today.setHours(0, 0, 0, 0); // Reset time to start of day for fair comparison

  if (selectedDate <= today) {
    return { pastDate: true };
  }

  return null;
}

@Component({
  selector: 'app-farm-visitiong-schedule',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './farm-visitiong-schedule.html',
  styleUrl: './farm-visitiong-schedule.css',
})
export class FarmVisitiongSchedule {
  @Input() isOpen = false; // show/hide modal
  @Input() consultationId!: number; // consultation ID
  @Output() close = new EventEmitter<void>(); // closes modal
  @Output() scheduled = new EventEmitter<any>(); // returns scheduled data

  farmVisitForm: FormGroup;
  minDate: string; // Minimum date for the date input

  constructor(private fb: FormBuilder, private farmVisitService: FarmVisit) {
    // Set minimum date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.minDate = tomorrow.toISOString().split('T')[0];

    this.farmVisitForm = this.fb.group({
      date: ['', [Validators.required, futureDateValidator]],
      time: ['', Validators.required],
      notes: [''],
    });
  }

  // Getter for easy access to form controls
  get f() {
    return this.farmVisitForm.controls;
  }

  closeModal() {
    this.close.emit();
  }

  submitSchedule() {
    console.log('🟢 submitSchedule called');
    console.log('🟢 Form valid:', this.farmVisitForm.valid);
    console.log('🟢 Form value:', this.farmVisitForm.value);

    if (this.farmVisitForm.invalid) {
      console.log('❌ Form is invalid, marking all as touched');
      this.farmVisitForm.markAllAsTouched();
      return;
    }

    const { date, time, notes } = this.farmVisitForm.value;

    const scheduledData = {
      scheduledDate: `${date}T${time}:00`,
      visitNotes: notes || '',
      visitStatus: 'SCHEDULED' as const,
    };

    console.log('📌 Scheduling Farm Visit with data:', scheduledData);

    // Call the farm visit service
    this.farmVisitService.scheduleFarmVisit(this.consultationId, scheduledData).subscribe({
      next: (response) => {
        console.log('✅ Farm visit scheduled successfully:', response.data);
        this.scheduled.emit(response.data);
        this.close.emit();
        this.farmVisitForm.reset();
      },
      error: (error) => {
        console.error('❌ Error scheduling farm visit:', error);
      },
    });
  }
}
