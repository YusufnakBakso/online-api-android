import Swal from 'sweetalert2';

export const swalSuccess = (title: string, text: string) => {
  Swal.fire({
    title,
    text,
    icon: 'success',
    confirmButtonText: 'OK',
  });
};

export const swalError = (title: string, text: string) => {
  Swal.fire({
    title,
    text,
    icon: 'error',
    confirmButtonText: 'OK',
  });
};

export const swalConfirm = (title: string, text: string, callback: () => void) => {
  Swal.fire({
    title,
    text,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Ya',
    cancelButtonText: 'Tidak',
  }).then((result) => {
    if (result.isConfirmed) {
      callback();
    }
  });
};

export default { swalSuccess, swalError, swalConfirm };
